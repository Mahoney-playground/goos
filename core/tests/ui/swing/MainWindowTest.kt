package goos.ui.swing

import goos.ui.api.UserRequestListener
import goos.uitestsupport.AuctionSniperDriver
import io.kotest.assertions.timing.eventually
import io.kotest.core.test.isActive
import io.kotest.core.spec.style.StringSpec
import io.mockk.clearAllMocks
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import net.sourceforge.marathon.javadriver.JavaDriver
import javax.swing.SwingUtilities
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
class MainWindowTest : StringSpec({

  tags(UI)

  val userRequestListener: UserRequestListener = mockk(relaxed = true)
  lateinit var driver: AuctionSniperDriver

  beforeSpec { spec ->
    if (spec.rootTests().any { it.testCase.isActive() }) {
      driver = initiailiseUi(userRequestListener)
    }
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

  "make user request when connect button clicked" {

    driver.connect()

    eventually(1.seconds) {
      verify(exactly = 1) {
        userRequestListener.connect()
      }
    }
    confirmVerified(userRequestListener)
  }

  afterTest {
    clearAllMocks()
  }
})

private fun initiailiseUi(
  userRequestListener: UserRequestListener
): AuctionSniperDriver {
  SwingUtilities.invokeAndWait {
    val mainWindow = MainWindow(SnipersTableModel())
    mainWindow.addUserRequestListener(userRequestListener)
  }
  return AuctionSniperDriver(JavaDriver())
}
