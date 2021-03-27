package goos.core

import goos.auction.api.AuctionHouse
import goos.ui.api.Item
import goos.ui.api.UserRequestListener
import uk.org.lidalia.kotlinlangext.concurrent.Gate

internal class SniperLauncher(
  private val auctionHouse: AuctionHouse,
  private val portfolio: SniperPortfolio,
  private val shutdownGate: Gate,
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

  override fun closeApplication() {
    auctionHouse.disconnect()
    shutdownGate.open()
  }
}
