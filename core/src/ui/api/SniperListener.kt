package goos.core.ui.api

import java.util.EventListener

interface SniperListener : EventListener {
  fun sniperStateChanged(sniperSnapshot: SniperSnapshot)
}
