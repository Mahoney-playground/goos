package goos.core

import goos.auction.api.Auction
import goos.auction.api.AuctionEventListener
import goos.auction.api.AuctionEventListener.PriceSource
import goos.auction.api.AuctionEventListener.PriceSource.FromSniper
import goos.ui.MultiSniperListener
import goos.ui.SniperListener
import goos.ui.SniperNotifier

internal class AuctionSniper(
  val item: Item,
  private val auction: Auction
) : AuctionEventListener, SniperNotifier {

  private val sniperListeners = MultiSniperListener()

  private var snapshot = SniperSnapshot.joining(item)
    set(value) {
      field = value
      sniperListeners.sniperStateChanged(value)
    }

  override fun auctionClosed() {
    snapshot = snapshot.closed()
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
      if (item.allowsBid(bid)) {
        auction.bid(bid)
        snapshot.bidding(price, bid)
      } else {
        snapshot.losing(price)
      }
    }
  }

  override fun auctionFailed() {
    snapshot = snapshot.failed()
  }

  override fun addSniperListener(sniperListener: SniperListener) {
    sniperListener.sniperStateChanged(snapshot)
    sniperListeners.addListener(sniperListener)
  }
}
