package goos.core.core

import goos.core.auction.api.Auction
import goos.core.auction.api.AuctionEventListener
import goos.core.auction.api.AuctionEventListener.PriceSource
import goos.core.auction.api.AuctionEventListener.PriceSource.FromSniper

class AuctionSniper(
  itemId: String,
  private val auction: Auction,
  private val sniperListener: SniperListener
) : AuctionEventListener {

  private var snapshot = SniperSnapshot.joining(itemId)

  override fun auctionClosed() {
    snapshot = snapshot.closed()
    notifyChange()
  }

  override fun currentPrice(
    price: Int,
    increment: Int,
    source: PriceSource
  ) {
    snapshot = if (source == FromSniper) {
      snapshot.winning(price)
    } else {
      val bid = price + increment
      auction.bid(bid)
      snapshot.bidding(price, bid)
    }
    notifyChange()
  }

  private fun notifyChange() {
    sniperListener.sniperStateChanged(snapshot)
  }
}
