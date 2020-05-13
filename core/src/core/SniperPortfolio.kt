package goos.core

import goos.core.api.MultiPortfolioListener
import goos.core.api.PortfolioListener
import goos.core.api.PortfolioNotifier

class SniperPortfolio : PortfolioNotifier {

  private val snipers = mutableListOf<AuctionSniper>()
  private val portfolioListeners = MultiPortfolioListener()

  fun addSniper(sniper: AuctionSniper) {
    snipers.add(sniper)
    portfolioListeners.sniperAdded(sniper)
  }

  fun reset() {
    snipers.clear()
    portfolioListeners.reset()
  }

  override fun addPortfolioListener(listener: PortfolioListener) {
    portfolioListeners.addListener(listener)
  }
}
