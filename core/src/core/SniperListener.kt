package goos.core

import java.util.EventListener

interface SniperListener : EventListener {
  fun sniperStateChanged(sniperSnapshot: SniperSnapshot)
}

class MultiSniperListener : SniperListener {

  private val listeners = mutableListOf<SniperListener>()

  override fun sniperStateChanged(sniperSnapshot: SniperSnapshot) {
    listeners.forEach { it.sniperStateChanged(sniperSnapshot) }
  }

  fun addListener(sniperListener: SniperListener) {
    listeners.add(sniperListener)
  }
}
