package goos.ui.swing

import goos.core.SniperPortfolio
import goos.ui.api.UserRequestListener
import goos.uitestsupport.AuctionSniperDriver
import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.StringSpec
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import net.sourceforge.marathon.javadriver.JavaDriver
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

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
    withContext(Dispatchers.Swing) {
      mainWindow = MainWindow(SniperPortfolio())
      mainWindow.addUserRequestListener(userRequestListener)
    }
    driver = AuctionSniperDriver(JavaDriver())
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

    driver.startBiddingFor("an item-id")

    eventually(5.seconds) {
      verify(exactly = 1) {
        userRequestListener.joinAuction("an item-id")
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
})
