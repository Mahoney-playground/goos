package goos.core

import goos.auction.api.AuctionHouse
import goos.ui.api.UserRequestListener

class SniperLauncher(
  private val auctionHouse: AuctionHouse,
  private val collector: SniperCollector
) : UserRequestListener {

  override fun joinAuction(itemId: String) {

    val auction = auctionHouse.auctionFor(itemId)

    val sniper = AuctionSniper(itemId, auction)

    auction.addAuctionEventListener(sniper)

    collector.addSniper(sniper)

    auction.join()
  }

  override fun reset() {
    collector.reset()
  }

  override fun disconnect() {
    auctionHouse.disconnect()
  }
}
