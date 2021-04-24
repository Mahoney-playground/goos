package goos.auction.api

interface AuctionDriver : AutoCloseable {
  val auctionId: AuctionId
  fun startSellingItem()
  fun announceClosed()
  fun reportPrice(price: Int, increment: Int, bidder: BidderId)
  fun sendInvalidMessageContaining(brokenMessage: String)
  suspend fun hasReceivedJoinRequestFrom(sniperId: BidderId)
  suspend fun hasReceivedBid(bid: Int, sniperId: BidderId)
}
