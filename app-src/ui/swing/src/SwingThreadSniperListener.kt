package goos.ui.swing

import goos.ui.api.SniperListener
import goos.ui.api.SniperSnapshot
import javax.swing.SwingUtilities

internal class SwingThreadSniperListener(
  private val decorated: SniperListener,
) : SniperListener {

  override fun sniperStateChanged(sniperSnapshot: SniperSnapshot) =
    SwingUtilities.invokeLater {
      decorated.sniperStateChanged(sniperSnapshot)
    }
}
