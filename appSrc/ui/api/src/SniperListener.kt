package goos.ui.api

import uk.org.lidalia.kotlinlangext.notifiers.Notifier

interface SniperListener {
  fun sniperStateChanged(sniperSnapshot: SniperSnapshot)
}

class MultiSniperListener : SniperListener, Notifier<SniperListener>() {

  override fun sniperStateChanged(sniperSnapshot: SniperSnapshot) {
    notify { sniperStateChanged(sniperSnapshot) }
  }
}
