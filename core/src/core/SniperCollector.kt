package goos.core

interface SniperCollector {
  fun addSniper(sniper: AuctionSniper)
  fun reset()
}
