package goos.ui

import goos.core.SniperListener

interface SniperNotifier {
  fun addSniperListener(sniperListener: SniperListener)
}
