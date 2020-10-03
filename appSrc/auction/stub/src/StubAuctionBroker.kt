package goos.auction.stub

import goos.auction.sol.MessageListener
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

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
