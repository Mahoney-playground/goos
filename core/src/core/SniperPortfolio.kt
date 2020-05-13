package goos.core

import java.util.EventListener

class SniperPortfolio : SniperCollector {

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

  fun addPortfolioListener(listener: PortfolioListener) {
    portfolioListeners.addListener(listener)
  }
}

interface PortfolioListener : EventListener {
  fun sniperAdded(sniper: AuctionSniper)
  fun reset()
}

class MultiPortfolioListener : PortfolioListener {

  private val listeners = mutableListOf<PortfolioListener>()

  fun addListener(listener: PortfolioListener) = listeners.add(listener)

  override fun sniperAdded(sniper: AuctionSniper) = listeners.forEach { it.sniperAdded(sniper) }
  override fun reset() = listeners.forEach { it.reset() }
}
