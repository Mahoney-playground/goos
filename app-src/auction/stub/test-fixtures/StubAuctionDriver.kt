package goos.auction.stub

import goos.auction.api.AuctionDriver
import goos.auction.api.AuctionId
import goos.auction.api.BidderId
import io.kotest.assertions.timing.eventually
import io.kotest.matchers.collections.shouldContain
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

class StubAuctionDriver(
  override val auctionId: AuctionId,
  private val auctionServer: StubAuctionServer = StubAuctionServer()
) : AuctionDriver {

  override fun startSellingItem() {
    auctionServer.startAuction(auctionId)
  }

  override fun announceClosed() {
    auctionServer.close(auctionId)
  }

  override fun reportPrice(price: Int, increment: Int, bidder: BidderId) {
    auctionServer.sendToSubscribers(
      auctionId,
      "SOLVersion: 1.1; Event: PRICE; CurrentPrice: $price; Increment: $increment; Bidder: $bidder;"
    )
  }

  override fun sendInvalidMessageContaining(brokenMessage: String) {
    auctionServer.sendToSubscribers(
      auctionId,
      brokenMessage
    )
  }

  @ExperimentalTime
  override suspend fun hasReceivedJoinRequestFrom(sniperId: BidderId) {
    hasReceivedMessage(sniperId, "SOLVersion: 1.1; Command: JOIN;")
  }

  @ExperimentalTime
  override suspend fun hasReceivedBid(bid: Int, sniperId: BidderId) {
    hasReceivedMessage(sniperId, "SOLVersion: 1.1; Command: BID; Price: $bid;")
  }

  @ExperimentalTime
  private suspend fun hasReceivedMessage(sniperId: BidderId, expectedMessage: String) {
    eventually(1.seconds) {
      auctionServer.messagesFor(auctionId) shouldContain Message(sniperId, expectedMessage)
    }
  }

  override fun close() {
    auctionServer.reset()
  }
}
