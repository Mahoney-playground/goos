package goos.core

import goos.core.AuctionEventListener.PriceSource
import goos.core.AuctionEventListener.PriceSource.FromSniper
import java.util.EventListener

class AuctionSniper(
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
      auction.bid(price + increment)
      sniperListener.sniperBidding()
    }
  }
}

interface SniperListener : EventListener {
  fun sniperLost()
  fun sniperBidding()
  fun sniperWinning()
  fun sniperWon()
}
