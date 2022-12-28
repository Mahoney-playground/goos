package goos.auction.api

import kotlin.time.ExperimentalTime

interface AuctionDriver : AutoCloseable {
  val auctionId: AuctionId
  fun startSellingItem()
  fun announceClosed()
  fun reportPrice(price: Int, increment: Int, bidder: BidderId)
  fun sendInvalidMessageContaining(brokenMessage: String)

  @ExperimentalTime
  suspend fun hasReceivedJoinRequestFrom(sniperId: BidderId)

  @ExperimentalTime
  suspend fun hasReceivedBid(bid: Int, sniperId: BidderId)
}
