package goos.xmpptestsupport

interface AuctionDriver : AutoCloseable {
  val itemId: String
  fun startSellingItem()
  fun announceClosed()
  fun reportPrice(price: Int, increment: Int, bidder: String)
  fun sendInvalidMessageContaining(brokenMessage: String)
  fun hasReceivedJoinRequestFrom(sniperId: String)
  fun hasReceivedBid(bid: Int, sniperId: String)
}
