package goos.core

class SniperPortfolio : SniperCollector {

  private val notToBeGCd = mutableListOf<AuctionSniper>()
  private val portfolioListeners = MultiPortfolioListener()

  override fun addSniper(sniper: AuctionSniper) {
    notToBeGCd.add(sniper)
    portfolioListeners.sniperAdded(sniper)
  }

  override fun reset() {
    notToBeGCd.clear()
    portfolioListeners.reset()
  }

  fun addPortfolioListener(listener: PortfolioListener) {
    portfolioListeners.addListener(listener)
  }
}

interface PortfolioListener {
  fun sniperAdded(sniper: AuctionSniper)
  fun reset()
}

class MultiPortfolioListener : PortfolioListener {

  private val listeners = mutableListOf<PortfolioListener>()

  fun addListener(listener: PortfolioListener) = listeners.add(listener)

  override fun sniperAdded(sniper: AuctionSniper) = listeners.forEach { it.sniperAdded(sniper) }
  override fun reset() = listeners.forEach { it.reset() }
}
