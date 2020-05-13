package goos.core.api

import java.util.EventListener

interface PortfolioListener : EventListener {
  fun sniperAdded(sniper: SniperNotifier)
  fun reset()
}

class MultiPortfolioListener : PortfolioListener {

  private val listeners = mutableListOf<PortfolioListener>()

  fun addListener(listener: PortfolioListener) = listeners.add(listener)

  override fun sniperAdded(sniper: SniperNotifier) = listeners.forEach { it.sniperAdded(sniper) }
  override fun reset() = listeners.forEach { it.reset() }
}
