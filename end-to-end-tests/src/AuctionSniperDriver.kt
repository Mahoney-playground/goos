package goos

import io.kotest.assertions.timing.eventually
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.openqa.selenium.Platform
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

  init {
    driver.rootElement().getAttribute("name") shouldBe MAIN_WINDOW_NAME
    driver.rootElement().isDisplayed shouldBe true
  }

  @ExperimentalTime
  fun showSniperStatus(statusText: String) {
    runBlocking {
      eventually(5.seconds) {
        val statusElement = driver.findElementByName(SNIPER_STATUS_NAME)
        statusElement.text shouldBe statusText
      }
    }
  }

  fun joinAuction() {
    driver.findElementByName(SNIPER_JOIN_BUTTON_NAME).click()
  }

  companion object {

    const val MAIN_WINDOW_NAME: String = "Auction Sniper Name"

    const val SNIPER_STATUS_NAME: String = "sniper status"
    const val SNIPER_JOIN_BUTTON_NAME: String = "sniper join button"
  }
}

private fun RemoteWebDriver.rootElement() = findElementByCssSelector(".")
