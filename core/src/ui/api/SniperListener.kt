package goos.ui.api

import java.util.EventListener

interface SniperListener : EventListener {
  fun sniperStateChanged(sniperSnapshot: UiSniperSnapshot)
}
