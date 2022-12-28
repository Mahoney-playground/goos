package goos.core

import goos.auction.api.AuctionHouse
import goos.auction.api.AuctionId
import goos.ui.api.Item
import goos.ui.api.ItemId
import goos.ui.api.UserRequestListener
import uk.org.lidalia.kotlinlangext.concurrent.Signal

internal class SniperLauncher(
  private val auctionHouse: AuctionHouse,
  private val portfolio: SniperPortfolio,
  private val shutdownSignal: Signal,
) : UserRequestListener {

  override fun joinAuction(item: Item) {
    val auction = auctionHouse.auctionFor(item.identifier.toAuctionId())

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
    shutdownSignal.trigger()
  }
}

private fun ItemId.toAuctionId() = AuctionId(this.value)
