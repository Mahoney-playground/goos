package goos.core

class SniperPortfolio : SniperCollector, PortfolioNotifier {

  private val snipers = mutableListOf<AuctionSniper>()
  private val portfolioListeners = MultiPortfolioListener()

  override fun addSniper(sniper: AuctionSniper) {
    snipers.add(sniper)
    portfolioListeners.sniperAdded(sniper)
  }

  override fun reset() {
    snipers.clear()
    portfolioListeners.reset()
  }

  override fun addPortfolioListener(listener: PortfolioListener) {
    portfolioListeners.addListener(listener)
  }
}
