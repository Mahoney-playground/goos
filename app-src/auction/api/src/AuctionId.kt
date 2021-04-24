package goos.auction.api

inline class AuctionId(val value: String) {
  override fun toString() = value
}

fun CharSequence.toAuctionId() = AuctionId(this.toString())
