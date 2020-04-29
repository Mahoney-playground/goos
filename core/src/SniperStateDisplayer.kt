package goos.core

import javax.swing.SwingUtilities

internal class SniperStateDisplayer(
  private val ui: MainWindow
) : SniperListener {
  override fun sniperLost() {
    SwingUtilities.invokeLater {
      ui.showStatus(MainWindow.STATUS_LOST)
    }
  }

  override fun sniperBidding() {
    SwingUtilities.invokeLater {
      ui.showStatus(MainWindow.STATUS_BIDDING)
    }
  }
}
