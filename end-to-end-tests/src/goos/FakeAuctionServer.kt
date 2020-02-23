package goos

import io.kotlintest.shouldNotBe
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit.SECONDS

class FakeAuctionServer(
  val itemId: String
) {

  private val connection = XMPPTCPConnection(XMPP_HOSTNAME, "auction-$itemId", AUCTION_PASSWORD)
  private lateinit var currentChat: Chat

  private val messageListener = SingleMessageListener()

  fun startSellingItem() {

    connection.connect()
    connection.login("auction-$itemId", AUCTION_PASSWORD)

    ChatManager.getInstanceFor(connection).addIncomingListener { _, _, chat -> currentChat = chat }
  }

  fun hasReceivedJoinRequestFromSniper() {
    messageListener.receivesAMessage()
  }

  fun announceClosed() {
    currentChat.send(Message())
  }

  fun stop() {
    connection.disconnect()
  }

  companion object {
    const val AUCTION_RESOURCE = "Auction"
    const val XMPP_HOSTNAME = "localhost"
    const val AUCTION_PASSWORD = "auction"
  }
}

class SingleMessageListener() : MessageListener {

  private val messages: BlockingQueue<Message> = ArrayBlockingQueue(1)

  override fun processMessage(message: Message) {
    messages.add(message)
  }

  fun receivesAMessage() {
    messages.poll(5, SECONDS) shouldNotBe null
  }
}
