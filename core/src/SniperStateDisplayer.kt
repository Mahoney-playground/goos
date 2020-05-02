package goos.core

import goos.core.MainWindow.Companion.STATUS_BIDDING
import goos.core.MainWindow.Companion.STATUS_LOST
import goos.core.MainWindow.Companion.STATUS_WINNING
import goos.core.MainWindow.Companion.STATUS_WON
import javax.swing.SwingUtilities

internal class SniperStateDisplayer(
  private val ui: MainWindow
) : SniperListener {

  override fun sniperBidding(sniperState: SniperState) = statusChanged(sniperState, STATUS_BIDDING)
  override fun sniperWinning() = showStatus(STATUS_WINNING)
  override fun sniperLost() = showStatus(STATUS_LOST)
  override fun sniperWon() = showStatus(STATUS_WON)

  private fun showStatus(status: String) {
    SwingUtilities.invokeLater {
      ui.showStatus(status)
    }
  }

  private fun statusChanged(sniperState: SniperState, status: String) =
    SwingUtilities.invokeLater {
      ui.sniperStatusChanged(sniperState, status)
    }
}
