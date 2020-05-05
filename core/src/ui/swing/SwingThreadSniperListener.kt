package goos.core.ui.swing

import goos.core.ui.api.SniperListener
import goos.core.ui.api.UiSniperSnapshot
import javax.swing.SwingUtilities

internal class SwingThreadSniperListener(
  private val decorated: SniperListener
) : SniperListener {

  override fun sniperStateChanged(sniperSnapshot: UiSniperSnapshot) =
    SwingUtilities.invokeLater {
      decorated.sniperStateChanged(sniperSnapshot)
    }
}
