package goos.auction.stub

import goos.auction.api.AuctionDriver
import io.kotest.assertions.timing.eventually
import io.kotest.matchers.collections.shouldContain
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

class StubAuctionDriver(
  override val itemId: String,
  private val auctionServer: StubAuctionServer = StubAuctionServer()
) : AuctionDriver {

  override fun startSellingItem() {
    auctionServer.startAuction(itemId)
  }

  override fun announceClosed() {
    auctionServer.close(itemId)
  }

  override fun reportPrice(price: Int, increment: Int, bidder: String) {
    auctionServer.sendToSubscribers(
      itemId,
      "SOLVersion: 1.1; Event: PRICE; CurrentPrice: $price; Increment: $increment; Bidder: $bidder;"
    )
  }

  override fun sendInvalidMessageContaining(brokenMessage: String) {
    auctionServer.sendToSubscribers(
      itemId,
      brokenMessage
    )
  }

  @ExperimentalTime
  override suspend fun hasReceivedJoinRequestFrom(sniperId: String) {
    hasReceivedMessage(sniperId, "SOLVersion: 1.1; Command: JOIN;")
  }

  @ExperimentalTime
  override suspend fun hasReceivedBid(bid: Int, sniperId: String) {
    hasReceivedMessage(sniperId, "SOLVersion: 1.1; Command: BID; Price: $bid;")
  }

  @ExperimentalTime
  private suspend fun hasReceivedMessage(sniperId: String, expectedMessage: String) {
    val messages = auctionServer.messagesFor(itemId)
    eventually(1.seconds) {
      messages shouldContain Message(sniperId, expectedMessage)
    }
  }

  override fun close() {
    auctionServer.reset()
  }
}
