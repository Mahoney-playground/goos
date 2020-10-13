package goos.auction.stub

import goos.auction.sol.MessageListener
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.CopyOnWriteArrayList

class StubAuctionServer {

  private val auctions: ConcurrentMap<String, StubAuctionBroker> = ConcurrentHashMap()

  fun startAuction(itemId: String) {
    auctions[itemId] = StubAuctionBroker()
  }

  fun close(itemId: String) {
    sendToSubscribers(itemId, "SOLVersion: 1.1; Event: CLOSE;")
  }

  fun sendToSubscribers(itemId: String, message: String) {
    auctions[itemId]?.sendAuctionServerMessage(message)
  }

  fun reset() {
    auctions.clear()
  }

  fun messagesFor(itemId: String): List<Message> = auctions[itemId]?.messages ?: emptyList()

  fun subscribe(itemId: String, messageListener: MessageListener) {
    auctions[itemId]?.subscribe(messageListener)
  }

  fun receiveMessage(itemId: String, message: Message) {
    auctions[itemId]?.receiveMessage(message)
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

data class Message(val from: String, val text: String)
