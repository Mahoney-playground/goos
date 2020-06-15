package goos.ui.api

import goos.portfolio.PortfolioListener

interface UI {
  val portfolioListener: PortfolioListener
  fun addUserRequestListener(listener: UserRequestListener)
  fun start()
}
