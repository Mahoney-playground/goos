package goos.auction.api

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
}

class MultiAuctionEventListener : AuctionEventListener {

  private val listeners = mutableListOf<AuctionEventListener>()

  fun addListener(listener: AuctionEventListener) {
    listeners.add(listener)
  }

  override fun auctionClosed() = listeners.forEach { it.auctionClosed() }

  override fun currentPrice(
    price: Int,
    increment: Int,
    source: AuctionEventListener.PriceSource
  ) {
    listeners.forEach { it.currentPrice(price, increment, source) }
  }
}
