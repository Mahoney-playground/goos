package goos.core

import javax.swing.SwingUtilities

internal class SniperStateDisplayer(
  private val ui: MainWindow
) : SniperListener {

  override fun sniperStateChanged(sniperSnapshot: SniperSnapshot) =
    stateChanged(sniperSnapshot)

  private fun stateChanged(sniperSnapshot: SniperSnapshot) =
    SwingUtilities.invokeLater {
      ui.sniperStateChanged(sniperSnapshot)
    }
}
