package goos.ui.stub

import goos.ui.api.PortfolioListener
import goos.ui.api.SniperNotifier
import goos.ui.api.UI
import goos.ui.api.UserRequestListener

class StubUi : UI {

  override val portfolioListener: PortfolioListener = StubPortfolioListener()

  override fun addUserRequestListener(listener: UserRequestListener) {
    TODO("not implemented")
  }

  override fun start() {
    TODO("not implemented")
  }

  fun reset() {}
}

class StubPortfolioListener : PortfolioListener {
  override fun sniperAdded(sniper: SniperNotifier) {
    TODO("not implemented")
  }

  override fun reset() {
    TODO("not implemented")
  }
}
