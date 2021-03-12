package goos.ui.api

import uk.org.lidalia.kotlinlangext.notifiers.Notifier

interface PortfolioListener {
  fun sniperAdded(sniper: SniperNotifier)
  fun reset()
}

class MultiPortfolioListener : PortfolioListener, Notifier<PortfolioListener>() {
  override fun sniperAdded(sniper: SniperNotifier) = notify { sniperAdded(sniper) }
  override fun reset() = notify { reset() }
}
