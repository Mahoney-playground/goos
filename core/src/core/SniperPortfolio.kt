package goos.core

internal class SniperPortfolio : PortfolioNotifier {

  private val snipers = mutableListOf<AuctionSniper>()
  private val portfolioListeners = MultiPortfolioListener()

  internal fun addSniper(sniper: AuctionSniper) {
    snipers.add(sniper)
    portfolioListeners.sniperAdded(sniper)
  }

  internal fun reset() {
    snipers.clear()
    portfolioListeners.reset()
  }

  override fun addPortfolioListener(listener: PortfolioListener) {
    portfolioListeners.addListener(listener)
  }
}
