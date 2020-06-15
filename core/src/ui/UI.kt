package goos.ui

import goos.core.PortfolioListener

interface UI {
  val portfolioListener: PortfolioListener
  fun addUserRequestListener(listener: UserRequestListener)
  fun start()
}
