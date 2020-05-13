package goos.ui.api

import java.util.EventListener

interface SniperListener : EventListener {
  fun sniperStateChanged(sniperSnapshot: UiSniperSnapshot)
}

class MultiSniperListener : SniperListener {

  private val listeners = mutableListOf<SniperListener>()

  override fun sniperStateChanged(sniperSnapshot: UiSniperSnapshot) {
    listeners.forEach { it.sniperStateChanged(sniperSnapshot) }
  }

  fun addListener(sniperListener: SniperListener) {
    listeners.add(sniperListener)
  }
}
