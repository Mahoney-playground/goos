package goos.core

import goos.auction.api.AuctionHouse
import goos.ui.UserRequestListener

internal class SniperLauncher(
  private val auctionHouse: AuctionHouse,
  private val portfolio: SniperPortfolio
) : UserRequestListener {

  override fun joinAuction(item: Item) {

    val auction = auctionHouse.auctionFor(item.identifier)

    val sniper = AuctionSniper(item, auction)

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
