package goos.auction.api

@JvmInline
value class AuctionId(val value: String) {
  override fun toString() = value
}

fun CharSequence.toAuctionId() = AuctionId(this.toString())
