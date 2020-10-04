package goos.ui.api

interface SniperListener {
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
