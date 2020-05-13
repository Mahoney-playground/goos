package goos.ui.swing

import goos.core.Core
import javax.swing.SwingUtilities

fun buildUi(core: Core) {
  SwingUtilities.invokeAndWait {
    MainWindow(core.portfolioNotifier).apply {
      addUserRequestListener(core.userRequestListener)
    }
  }
}
