package goos.ui.swing

import goos.ui.api.UserRequestListener
import goos.ui.api.toItemId
import goos.ui.common.ItemData
import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.StringSpec
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import net.sourceforge.marathon.javadriver.JavaDriver
import org.openqa.selenium.remote.CapabilityType.BROWSER_NAME
import org.openqa.selenium.remote.DesiredCapabilities
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@ExperimentalTime
class MainWindowTest : StringSpec({

  tags(UI)

  /*
   * These are initialised in the beforeTest block so that
   * the window is not created if the tests are ignored
   */
  lateinit var userRequestListener: UserRequestListener
  lateinit var mainWindow: MainWindow
  lateinit var driver: AuctionSniperDriver

  beforeTest {
    userRequestListener = mockk(relaxed = true)
    mainWindow = MainWindow()
    mainWindow.addUserRequestListener(userRequestListener)
    mainWindow.start()
    driver = AuctionSniperDriver(
      JavaDriver(
        DesiredCapabilities().apply {
          setCapability(BROWSER_NAME, "java")
        },
      ),
    )
  }

  afterTest {
    driver.close()
  }

  "window has expected features" {
    driver.hasBasicAttributes()
    driver.hasTitle("Auction Sniper")
    driver.hasColumnTitles()
  }

  "make user request when join button clicked" {

    driver.startBiddingFor("an item-id".toItemId(), 11_000)

    eventually(5.seconds) {
      verify(exactly = 1) {
        userRequestListener.joinAuction(
          ItemData(
            "an item-id".toItemId(),
            stopPrice = 11_000,
          ),
        )
      }
    }
    confirmVerified(userRequestListener)
  }

  "make user request when reset button clicked" {

    driver.reset()

    eventually(1.seconds) {
      verify(exactly = 1) {
        userRequestListener.reset()
      }
    }
    confirmVerified(userRequestListener)
  }
},)
