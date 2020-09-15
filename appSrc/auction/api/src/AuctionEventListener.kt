package goos.auction.api

import goos.auction.api.AuctionEventListener.PriceSource
import java.util.concurrent.CopyOnWriteArrayList

interface AuctionEventListener {

  enum class PriceSource {
    FromSniper, FromOtherBidder
  }

  fun auctionClosed()

  fun currentPrice(
    price: Int,
    increment: Int,
    source: PriceSource
  )

  fun auctionFailed()
}

class MultiAuctionEventListener : AuctionEventListener {

  private val listeners = CopyOnWriteArrayList<AuctionEventListener>()

  fun addListener(listener: AuctionEventListener) {
    listeners.add(listener)
  }

  override fun auctionClosed() = listeners.forEach { it.auctionClosed() }

  override fun currentPrice(
    price: Int,
    increment: Int,
    source: PriceSource
  ) {
    listeners.forEach { it.currentPrice(price, increment, source) }
  }

  override fun auctionFailed() = listeners.forEach { it.auctionFailed() }

  fun clear() = listeners.clear()
}

interface NoOpAuctionEventListener : AuctionEventListener {
  override fun auctionClosed() { /* no-op */ }
  override fun currentPrice(price: Int, increment: Int, source: PriceSource) { /* no-op */ }
  override fun auctionFailed() { /* no-op */ }
}
