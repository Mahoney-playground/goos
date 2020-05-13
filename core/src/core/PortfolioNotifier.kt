package goos.core

interface PortfolioNotifier {
  fun addPortfolioListener(listener: PortfolioListener)
}
