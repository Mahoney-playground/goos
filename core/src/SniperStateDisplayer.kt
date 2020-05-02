package goos.core

import goos.core.MainWindow.Companion.STATE_LOST
import goos.core.MainWindow.Companion.STATE_WON
import javax.swing.SwingUtilities

internal class SniperStateDisplayer(
  private val ui: MainWindow
) : SniperListener {

  override fun sniperStateChanged(sniperSnapshot: SniperSnapshot) =
    stateChanged(sniperSnapshot)

  override fun sniperLost() = showState(STATE_LOST)
  override fun sniperWon() = showState(STATE_WON)

  private fun showState(state: String) {
    SwingUtilities.invokeLater {
      ui.showState(state)
    }
  }

  private fun stateChanged(sniperSnapshot: SniperSnapshot) =
    SwingUtilities.invokeLater {
      ui.sniperStateChanged(sniperSnapshot)
    }
}
