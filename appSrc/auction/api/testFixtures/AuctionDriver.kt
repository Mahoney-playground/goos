package goos.auction.api

interface AuctionDriver : AutoCloseable {
  val itemId: String
  fun startSellingItem()
  fun announceClosed()
  fun reportPrice(price: Int, increment: Int, bidder: String)
  fun sendInvalidMessageContaining(brokenMessage: String)
  suspend fun hasReceivedJoinRequestFrom(sniperId: String)
  suspend fun hasReceivedBid(bid: Int, sniperId: String)
}
