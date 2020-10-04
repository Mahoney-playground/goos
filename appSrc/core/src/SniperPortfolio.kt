package goos.core

import goos.ui.api.MultiPortfolioListener
import goos.ui.api.PortfolioListener

internal class SniperPortfolio {

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

  fun addPortfolioListener(listener: PortfolioListener) {
    portfolioListeners.addListener(listener)
  }
}
