package goos.auction.api

inline class AuctionId(val value: String) {
  override fun toString() = value
}

fun String.toAuctionId() = AuctionId(this)
