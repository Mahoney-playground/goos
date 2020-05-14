package goos.core

import goos.auction.api.Auction
import goos.auction.api.AuctionEventListener
import goos.auction.api.AuctionEventListener.PriceSource
import goos.auction.api.AuctionEventListener.PriceSource.FromSniper

internal class AuctionSniper(
  val item: Item,
  private val auction: Auction
) : AuctionEventListener, SniperNotifier {

  private val sniperListeners = MultiSniperListener()

  var snapshot = SniperSnapshot.joining(item.identifier)
    private set

  init {
    notifyChange()
  }

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
      if (bid <= item.stopPrice) {
        auction.bid(bid)
        snapshot.bidding(price, bid)
      } else {
        snapshot.losing(price)
      }
    }
    notifyChange()
  }

  private fun notifyChange() {
    sniperListeners.sniperStateChanged(snapshot)
  }

  override fun addSniperListener(sniperListener: SniperListener) {
    sniperListeners.addListener(sniperListener)
    notifyChange()
  }
}
