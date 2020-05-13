package goos.core.api

interface PortfolioNotifier {
  fun addPortfolioListener(listener: PortfolioListener)
}
