package goos.xmpptestsupport

import goos.auction.sol.MessageListener
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.CopyOnWriteArrayList

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

  fun sendMessage(sniperId: String, message: String) {
    auctionServer.liveAuctions[itemId]?.receiveMessage(Message(sniperId, message))
  }

  fun register(messageListener: MessageListener) {
    auctionServer.liveAuctions[itemId]?.subscribe(messageListener)
  }
}

class StubAuctionServer {

  val liveAuctions: ConcurrentMap<String, StubAuctionBroker> = ConcurrentHashMap()
  val closedAuctions: ConcurrentMap<String, StubAuctionBroker> = ConcurrentHashMap()

  fun startAuction(itemId: String) {
    liveAuctions[itemId] = StubAuctionBroker()
  }

  fun close(itemId: String) {
    sendToSubscribers(itemId, "SOLVersion: 1.1; Event: CLOSE;")
    liveAuctions.remove(itemId)?.let { auction ->
      closedAuctions[itemId] = auction
    }
  }

  fun sendToSubscribers(itemId: String, message: String) {
    liveAuctions[itemId]?.sendAuctionServerMessage(message)
  }

  fun allAuctions() = liveAuctions + closedAuctions
}

class StubAuctionBroker {

  val messages: MutableList<Message> = CopyOnWriteArrayList()
  private val subscriptions: MutableSet<MessageListener> = ConcurrentHashMap.newKeySet()

  fun receiveMessage(message: Message) {
    messages.add(message)
  }

  fun subscribe(messageListener: MessageListener) {
    subscriptions.add(messageListener)
  }

  fun sendAuctionServerMessage(message: String) {
    subscriptions.forEach { it.processMessage(message) }
  }
}

data class Message(val from: String, val text: String)
