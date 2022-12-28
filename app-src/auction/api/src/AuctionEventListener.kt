package goos.auction.api

import goos.auction.api.AuctionEventListener.PriceSource
import uk.org.lidalia.kotlinlangext.notifiers.Notifier

interface AuctionEventListener {

  enum class PriceSource {
    FromSniper, FromOtherBidder
  }

  fun auctionClosed()

  fun currentPrice(
    price: Int,
    increment: Int,
    source: PriceSource,
  )

  fun auctionFailed()
}

class MultiAuctionEventListener : AuctionEventListener, Notifier<AuctionEventListener>() {

  override fun auctionClosed() = notify { auctionClosed() }

  override fun currentPrice(
    price: Int,
    increment: Int,
    source: PriceSource,
  ) = notify { currentPrice(price, increment, source) }

  override fun auctionFailed() = notify { auctionFailed() }
}

interface NoOpAuctionEventListener : AuctionEventListener {
  override fun auctionClosed() { /* no-op */
  }

  override fun currentPrice(price: Int, increment: Int, source: PriceSource) { /* no-op */
  }

  override fun auctionFailed() { /* no-op */
  }
}
