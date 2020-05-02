package goos.core

import goos.core.AuctionEventListener.PriceSource
import goos.core.AuctionEventListener.PriceSource.FromSniper
import goos.core.SniperState.BIDDING
import goos.core.SniperState.JOINING
import goos.core.SniperState.WINNING
import java.util.EventListener

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

interface SniperListener : EventListener {
  fun sniperStateChanged(sniperSnapshot: SniperSnapshot)
  fun sniperLost()
  fun sniperWon()
}

data class SniperSnapshot(
  val itemId: String,
  val lastPrice: Int,
  val lastBid: Int,
  val state: SniperState
) {

  fun bidding(newLastPrice: Int, newLastBid: Int) =
    SniperSnapshot(itemId, newLastPrice, newLastBid, BIDDING)

  fun winning(newLastPrice: Int) =
    SniperSnapshot(itemId, newLastPrice, lastBid, WINNING)

  fun closed() =
    SniperSnapshot(itemId, lastPrice, lastBid, state.whenAuctionClosed())

  companion object {
    fun joining(itemId: String) = SniperSnapshot(itemId, 0, 0, JOINING)
  }
}

enum class SniperState {
  JOINING {
    override fun whenAuctionClosed() = LOST
  },
  BIDDING {
    override fun whenAuctionClosed() = LOST
  },
  WINNING {
    override fun whenAuctionClosed() = WON
  },
  LOST {
    override fun whenAuctionClosed() = throw Defect("Auction is already closed")
  },
  WON {
    override fun whenAuctionClosed() = throw Defect("Auction is already closed")
  };

  abstract fun whenAuctionClosed(): SniperState
}
