package goos

import io.kotest.matchers.shouldBe
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode.disabled
import org.jivesoftware.smack.chat.Chat
import org.jivesoftware.smack.chat.ChatManager
import org.jivesoftware.smack.chat.ChatMessageListener
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.admin.ServiceAdministrationManager
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS

class FakeAuctionServer(
  val itemId: String
) {

  private val connection = XMPPTCPConnection(
    XMPPTCPConnectionConfiguration.builder()
      .setSecurityMode(disabled)
      .setXmppDomain(XMPP_DOMAIN)
      .build()
  )

  init {
    createUser("sniper", "sniper")
  }

  private val messageListener = SingleMessageListener()
  private var currentChat: Chat? = null

  fun startSellingItem() {

    createAuctionItem(itemId)
    connection.connect()
    connection.login("auction-$itemId", AUCTION_PASSWORD, Resourcepart.from(AUCTION_RESOURCE))

    val chatManager = ChatManager.getInstanceFor(connection)
    chatManager.addChatListener { chat, _ ->
      currentChat = chat
      chat.addMessageListener(messageListener)
    }
    messageListener.drain()
  }

  private fun createAuctionItem(itemId: String) = createUser("auction-$itemId", AUCTION_PASSWORD)

  private fun createUser(username: String, password: String) {
    val c = XMPPTCPConnection(
      XMPPTCPConnectionConfiguration.builder()
        .setSecurityMode(disabled)
        .setXmppDomain(XMPP_DOMAIN)
        .build()
      )
    c.connect()
    c.login("admin", "admin")
    ServiceAdministrationManager.getInstanceFor(c).addUser(
      JidCreate.entityBareFrom("$username@$XMPP_DOMAIN"),
      password
    )
    c.disconnect()
  }

  fun hasReceivedJoinRequestFrom(sniperId: String) = receivesAMessageMatching(sniperId) {
    it shouldBe "SOLVersion: 1.1; Command: JOIN;"
  }

  fun hasReceivedBid(bid: Int, sniperId: String) = receivesAMessageMatching(sniperId) {
    it shouldBe "SOLVersion: 1.1; Command: BID; Price: $bid;"
  }

  private fun receivesAMessageMatching(
    sniperId: String,
    matcher: (String?) -> Unit
  ) {
    messageListener.receivesAMessage(matcher)
    currentChat!!.participant shouldBe sniperId
  }

  fun announceClosed() = currentChat!!.sendMessage("SOLVersion: 1.1; Event: CLOSE;")

  fun stop() {
    try {
      messageListener.drain()
      connection.disconnect()
    } catch (t: Throwable) {
      t.printStackTrace()
    }
  }

  fun reportPrice(price: Int, increment: Int, bidder: String) =
    currentChat!!.sendMessage(
      "SOLVersion: 1.1; Event: PRICE; CurrentPrice: $price; Increment: $increment; Bidder: $bidder;"
    )

  companion object {
    const val AUCTION_RESOURCE = "Auction"
    const val XMPP_DOMAIN = "auctionhost.internal"
    const val AUCTION_PASSWORD = "auction"
  }
}

class SingleMessageListener : ChatMessageListener {

  private val messages: BlockingQueue<Message> = ArrayBlockingQueue(1)

  override fun processMessage(chat: Chat, message: Message) = messages.put(message)

  fun receivesAMessage(matcher: (String?) -> Unit) {
    val message = messages.poll(10, SECONDS) ?: throw AssertionError("No message received")
    matcher(message.body)
  }

  tailrec fun drain() {
    if (messages.poll(100, MILLISECONDS) != null) {
      drain()
    }
  }
}
