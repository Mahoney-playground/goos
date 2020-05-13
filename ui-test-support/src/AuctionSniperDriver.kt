package goos.uitestsupport

import io.kotest.assertions.timing.eventually
import io.kotest.inspectors.forOne
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.Platform
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import java.net.URL
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

class AuctionSniperDriver(
  private val driver: RemoteWebDriver
) {

  constructor(url: URL = URL("http://app.internal:1234")) : this(
    RemoteWebDriver(url, DesiredCapabilities("java", "1.0", Platform.ANY))
  )

  fun hasBasicAttributes() {
    driver.rootElement().getAttribute("name") shouldBe MAIN_WINDOW_NAME
    driver.rootElement().isDisplayed shouldBe true
  }

  @ExperimentalTime
  fun showSniperState(
    itemId: String,
    lastPrice: Int,
    lastBid: Int,
    stateText: String
  ) = runBlocking {
    eventually(5.seconds) {
      val table = JTableDriver(
        driver.findElementByName(SNIPERS_TABLE_NAME)
      )

      table.hasRow(
        { it shouldBe itemId },
        { it shouldBe lastPrice.toString() },
        { it shouldBe lastBid.toString() },
        { it shouldBe stateText }
      )
    }
  }

  fun hasTitle(title: String) {
    driver.title shouldBe title
  }

  fun hasColumnTitles() {
    val titles = driver.findElementByName(SNIPERS_TABLE_NAME)
      .findElements(By.cssSelector(".::header.::all-items"))
      .map { it.text }

    titles shouldBe listOf("Item", "Last Price", "Last Bid", "State")
  }

  fun reset() = resetButton().click()

  fun startBiddingFor(itemId: String) {
    itemIdField().clear()
    itemIdField().sendKeys(itemId)
    bidButton().click()
  }

  private fun resetButton() = driver.findElementByName(SNIPER_RESET_BUTTON_NAME)
  private fun itemIdField() = driver.findElementByName(NEW_ITEM_ID_NAME)
  private fun bidButton() = driver.findElementByName(JOIN_BUTTON_NAME)

  fun close() {
    driver.close()
  }

  companion object {

    const val MAIN_WINDOW_NAME: String = "Auction Sniper Name"

    const val SNIPERS_TABLE_NAME: String = "snipers table"
    const val SNIPER_RESET_BUTTON_NAME: String = "sniper reset button"
    const val NEW_ITEM_ID_NAME: String = "new item id field"
    const val JOIN_BUTTON_NAME: String = "join button"
  }
}

private fun RemoteWebDriver.rootElement() = findElementByCssSelector(".")

private class JTableDriver(
  private val element: WebElement
) {
  fun hasRow(vararg cellMatchers: (String) -> Unit) {
    (1 until element.rowCount() + 1).toList().forOne { rowNum ->
      cellMatchers.toList().forEachIndexed { index, cellMatcher ->
        cellMatcher(element.findElement(By.cssSelector(".::mnth-cell($rowNum, ${index + 1})")).text)
      }
    }
  }
}

private fun WebElement.rowCount(): Int {
  var numberOfRows = 0
  while (hasRow(numberOfRows + 1)) {
    numberOfRows += 1
  }
  return numberOfRows
}

private fun WebElement.hasRow(row: Int): Boolean =
  try {
    findElement(By.cssSelector(".::mnth-cell($row, 1)")).text
    true
  } catch (e: NoSuchElementException) {
    false
  }
