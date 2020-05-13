package goos.ui.swing

import goos.core.api.PortfolioNotifier
import goos.ui.api.UserRequestListener
import javax.swing.SwingUtilities

fun buildUi(
  portfolio: PortfolioNotifier,
  userRequestListener: UserRequestListener
) {
  SwingUtilities.invokeAndWait {
    MainWindow(portfolio).apply {
      addUserRequestListener(userRequestListener)
    }
  }
}
