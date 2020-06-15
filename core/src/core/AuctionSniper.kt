package goos.core

import goos.auction.api.Auction
import goos.auction.api.AuctionEventListener
import goos.auction.api.AuctionEventListener.PriceSource
import goos.auction.api.AuctionEventListener.PriceSource.FromSniper
import goos.core.SniperState.BIDDING
import goos.core.SniperState.FAILED
import goos.core.SniperState.JOINING
import goos.core.SniperState.LOSING
import goos.core.SniperState.LOST
import goos.core.SniperState.WINNING
import goos.core.SniperState.WON
import goos.ui.Item
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
      sniperListeners.sniperStateChanged(value.toUiSnapshot())
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
    sniperListener.sniperStateChanged(snapshot.toUiSnapshot())
    sniperListeners.addListener(sniperListener)
  }
}

private fun SniperSnapshot.toUiSnapshot() = goos.ui.SniperSnapshot(
  item,
  lastPrice,
  lastBid,
  state.toUiState()
)

private fun SniperState.toUiState() = when (this) {
  LOST -> goos.ui.SniperState.LOST
  WON -> goos.ui.SniperState.WON
  JOINING -> goos.ui.SniperState.JOINING
  BIDDING -> goos.ui.SniperState.BIDDING
  WINNING -> goos.ui.SniperState.WINNING
  LOSING -> goos.ui.SniperState.LOSING
  FAILED -> goos.ui.SniperState.FAILED
}
