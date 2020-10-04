package goos.ui.api

interface UI {
  val portfolioListener: PortfolioListener
  fun addUserRequestListener(listener: UserRequestListener)
  fun start()
}
