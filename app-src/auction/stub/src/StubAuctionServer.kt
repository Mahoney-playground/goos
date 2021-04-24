package goos.auction.stub

import goos.auction.api.AuctionId
import goos.auction.api.BidderId
import goos.auction.sol.MessageListener
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.CopyOnWriteArrayList

class StubAuctionServer {

  private val auctions: ConcurrentMap<AuctionId, StubAuctionBroker> = ConcurrentHashMap()

  fun startAuction(auctionId: AuctionId) {
    auctions[auctionId] = StubAuctionBroker()
  }

  fun close(auctionId: AuctionId) {
    sendToSubscribers(auctionId, "SOLVersion: 1.1; Event: CLOSE;")
  }

  fun sendToSubscribers(auctionId: AuctionId, message: String) {
    auctions[auctionId]?.sendAuctionServerMessage(message)
  }

  fun reset() {
    auctions.clear()
  }

  fun messagesFor(auctionId: AuctionId): List<Message> =
    auctions[auctionId]?.messages ?: emptyList()

  fun subscribe(auctionId: AuctionId, messageListener: MessageListener) {
    auctions[auctionId]?.subscribe(messageListener)
  }

  fun receiveMessage(auctionId: AuctionId, message: Message) {
    auctions[auctionId]?.receiveMessage(message)
  }
}

private class StubAuctionBroker {

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

data class Message(val from: BidderId, val text: String)
