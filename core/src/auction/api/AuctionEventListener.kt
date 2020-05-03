package goos.core.auction.api

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
