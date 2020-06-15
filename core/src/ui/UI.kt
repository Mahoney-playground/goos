package goos.ui

interface UI {
  val portfolioListener: PortfolioListener
  fun addUserRequestListener(listener: UserRequestListener)
  fun start()
}
