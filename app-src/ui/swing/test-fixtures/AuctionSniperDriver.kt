package goos.ui.swing

import goos.ui.api.ItemId
import goos.ui.api.UiDriver
import io.kotest.assertions.timing.eventually
import io.kotest.inspectors.forOne
import io.kotest.matchers.shouldBe
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.Platform
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import java.net.URL
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

class AuctionSniperDriver(
  private val driver: WebDriver,
) : UiDriver {

  constructor(url: URL = URL("http://app.internal:1234")) : this(
    RemoteWebDriver(url, DesiredCapabilities("java", "1.0", Platform.ANY)),
  )

  override fun hasBasicAttributes() {
    driver.rootElement().getAttribute("name") shouldBe MAIN_WINDOW_NAME
    driver.rootElement().isDisplayed shouldBe true
  }

  @ExperimentalTime
  override suspend fun showSniperState(
    itemId: ItemId,
    lastPrice: Int,
    lastBid: Int,
    stateText: String,
  ) = eventually(5.seconds) {
    val table = JTableDriver(
      driver.findElement(By.name(SNIPERS_TABLE_NAME)),
    )

    table.hasRow(
      { it shouldBe itemId.value },
      { it shouldBe lastPrice.toString() },
      { it shouldBe lastBid.toString() },
      { it shouldBe stateText },
    )
  }

  override fun hasTitle(title: String) {
    driver.title shouldBe title
  }

  override fun hasColumnTitles() {
    val titles = driver.findElement(By.name(SNIPERS_TABLE_NAME))
      .findElements(By.cssSelector(".::header.::all-items"))
      .map { it.text }

    titles shouldBe listOf("Item", "Last Price", "Last Bid", "State")
  }

  override fun reset() = resetButton().click()

  override fun startBiddingFor(itemId: ItemId, stopPrice: Int) {
    itemIdField().setText(itemId.value)
    stopPriceField().setText(stopPrice.toString())
    bidButton().click()
  }

  private fun WebElement.setText(text: String) {
    clear()
    sendKeys(text)
  }

  private fun resetButton() = driver.findElement(By.name(SNIPER_RESET_BUTTON_NAME))
  private fun itemIdField() = driver.findElement(By.name(NEW_ITEM_ID_NAME))
  private fun stopPriceField() = driver.findElement(By.name(NEW_ITEM_STOP_PRICE_NAME))
  private fun bidButton() = driver.findElement(By.name(JOIN_BUTTON_NAME))

  override fun close() {
    driver.close()
  }

  companion object {

    const val MAIN_WINDOW_NAME: String = "Auction Sniper Name"

    const val SNIPERS_TABLE_NAME: String = "snipers table"
    const val SNIPER_RESET_BUTTON_NAME: String = "sniper reset button"
    const val NEW_ITEM_ID_NAME: String = "new item id field"
    const val NEW_ITEM_STOP_PRICE_NAME: String = "new item stop price field"
    const val JOIN_BUTTON_NAME: String = "join button"
  }
}

private fun WebDriver.rootElement() = findElement(By.cssSelector("."))

private class JTableDriver(
  private val element: WebElement,
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
