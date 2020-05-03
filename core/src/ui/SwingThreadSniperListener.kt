package goos.core.ui

import goos.core.core.SniperListener
import goos.core.core.SniperSnapshot
import javax.swing.SwingUtilities

internal class SwingThreadSniperListener(
  private val decorated: SniperListener
) : SniperListener {

  override fun sniperStateChanged(sniperSnapshot: SniperSnapshot) =
    SwingUtilities.invokeLater {
      decorated.sniperStateChanged(sniperSnapshot)
    }
}
