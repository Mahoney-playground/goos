package goos.core.core

import java.util.EventListener

interface SniperListener : EventListener {
  fun sniperStateChanged(sniperSnapshot: SniperSnapshot)
}
