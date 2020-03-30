package goos

import com.objogate.wl.swing.AWTEventQueueProber
import com.objogate.wl.swing.driver.JFrameDriver
import com.objogate.wl.swing.driver.JLabelDriver
import com.objogate.wl.swing.gesture.GesturePerformer
import goos.core.Main
import goos.core.MainWindow.Companion.MAIN_WINDOW_NAME
import org.hamcrest.CoreMatchers.equalTo
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
class AuctionSniperDriver(timeout: Duration) : JFrameDriver(
  GesturePerformer(),
  topLevelFrame(
    named(MAIN_WINDOW_NAME),
    showingOnScreen()
  ),
  AWTEventQueueProber(timeout.toLongMilliseconds(), 100L)
) {

  fun showSniperStatus(statusText: String) {
    JLabelDriver(this, named(Main.SNIPER_STATUS_NAME)).hasText(equalTo(statusText))
  }
}
