package goos.core

import goos.core.AuctionEventListener.PriceSource
import goos.core.AuctionEventListener.PriceSource.FromSniper
import java.util.EventListener

class AuctionSniper(
  private val itemId: String,
  private val auction: Auction,
  private val sniperListener: SniperListener
) : AuctionEventListener {

  private var isWinning = false

  override fun auctionClosed() {
    if (isWinning) {
      sniperListener.sniperWon()
    } else {
      sniperListener.sniperLost()
    }
  }

  override fun currentPrice(
    price: Int,
    increment: Int,
    source: PriceSource
  ) {
    isWinning = source == FromSniper
    if (isWinning) {
      sniperListener.sniperWinning()
    } else {
      val bid = price + increment
      auction.bid(bid)
      sniperListener.sniperBidding(SniperSnapshot(itemId, price, bid))
    }
  }
}

interface SniperListener : EventListener {
  fun sniperBidding(sniperSnapshot: SniperSnapshot)
  fun sniperWinning()
  fun sniperLost()
  fun sniperWon()
}

data class SniperSnapshot(
  val itemId: String,
  val lastPrice: Int,
  val lastBid: Int
)
