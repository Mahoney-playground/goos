package goos.ui.swing

import goos.core.SniperListener
import goos.core.SniperSnapshot
import javax.swing.SwingUtilities

internal class SwingThreadSniperListener(
  private val decorated: SniperListener
) : SniperListener {

  override fun sniperStateChanged(sniperSnapshot: SniperSnapshot) =
    SwingUtilities.invokeLater {
      decorated.sniperStateChanged(sniperSnapshot)
    }
}
