package goos.xmpptestsupport

import goos.auction.stub.Message
import goos.auction.stub.StubAuctionServer
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull

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

  override fun hasReceivedJoinRequestFrom(sniperId: String) {
    hasReceivedMessage(sniperId, "SOLVersion: 1.1; Command: JOIN;")
  }

  override fun hasReceivedBid(bid: Int, sniperId: String) {
    hasReceivedMessage(sniperId, "SOLVersion: 1.1; Command: BID; Price: $bid;")
  }

  private fun hasReceivedMessage(sniperId: String, expectedMessage: String) {
    val auctionBroker = auctionServer.allAuctions()[itemId]
    auctionBroker.shouldNotBeNull()
    auctionBroker.messages shouldContain Message(sniperId, expectedMessage)
  }

  override fun close() {}
}
