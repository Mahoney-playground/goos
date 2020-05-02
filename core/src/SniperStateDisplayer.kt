package goos.core

import goos.core.MainWindow.Companion.STATE_LOST
import goos.core.MainWindow.Companion.STATE_WINNING
import goos.core.MainWindow.Companion.STATE_WON
import javax.swing.SwingUtilities

internal class SniperStateDisplayer(
  private val ui: MainWindow
) : SniperListener {

  override fun sniperBidding(sniperSnapshot: SniperSnapshot) =
    statusChanged(sniperSnapshot)
  override fun sniperWinning() = showStatus(STATE_WINNING)
  override fun sniperLost() = showStatus(STATE_LOST)
  override fun sniperWon() = showStatus(STATE_WON)

  private fun showStatus(status: String) {
    SwingUtilities.invokeLater {
      ui.showStatus(status)
    }
  }

  private fun statusChanged(sniperSnapshot: SniperSnapshot) =
    SwingUtilities.invokeLater {
      ui.sniperStatusChanged(sniperSnapshot)
    }
}
