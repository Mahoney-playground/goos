package goos.ui.swing

import goos.ui.api.UserRequestListener
import goos.uitestsupport.AuctionSniperDriver
import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.StringSpec
import io.mockk.mockk
import io.mockk.verify
import net.sourceforge.marathon.javadriver.JavaDriver
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
class MainWindowTest : StringSpec({

  val tableModel = SnipersTableModel()
  val userRequestListener = mockk<UserRequestListener>(relaxed = true)
  val mainWindow = MainWindow(tableModel)
  mainWindow.addUserRequestListener(userRequestListener)

  val driver = AuctionSniperDriver(JavaDriver())

  "make user request when join button clicked" {

    driver.startBiddingFor("an item-id")

    eventually(5.seconds) {
      verify(exactly = 1) {
        userRequestListener.joinAuction("an item-id")
      }
    }
  }

  "make user request when reset button clicked" {

    driver.reset()

    eventually(1.seconds) {
      verify(exactly = 1) {
        userRequestListener.reset()
      }
    }
  }

  "make user request when connect button clicked" {

    driver.connect()

    eventually(1.seconds) {
      verify(exactly = 1) {
        userRequestListener.connect()
      }
    }
  }
})
