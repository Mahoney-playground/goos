package goos.core

import goos.auction.api.AuctionHouse

internal class SniperLauncher(
  private val auctionHouse: AuctionHouse,
  private val portfolio: SniperPortfolio
) : UserRequestListener {

  override fun joinAuction(itemId: String) {

    val auction = auctionHouse.auctionFor(itemId)

    val sniper = AuctionSniper(itemId, auction)

    auction.addAuctionEventListener(sniper)

    portfolio.addSniper(sniper)

    auction.join()
  }

  override fun reset() {
    portfolio.reset()
  }

  override fun disconnect() {
    auctionHouse.disconnect()
  }
}
