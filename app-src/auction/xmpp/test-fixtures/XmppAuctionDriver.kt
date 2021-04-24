@file:Suppress("DEPRECATION")

package goos.auction.xmpp

import goos.auction.api.AuctionDriver
import goos.auction.api.AuctionId
import goos.auction.api.BidderId
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

class XmppAuctionDriver(
  override val auctionId: AuctionId
) : AuctionDriver {

  private val connection by lazy {
    createUser("sniper", "sniper")
    XMPPTCPConnection(
      XMPPTCPConnectionConfiguration.builder()
        .setSecurityMode(disabled)
        .setXmppDomain(XMPP_DOMAIN)
        .build()
    )
  }

  private val messageListener = SingleMessageListener()
  private var currentChat: Chat? = null

  override fun startSellingItem() {

    createAuctionItem(auctionId)
    connection.connect()
    connection.login("auction-$auctionId", AUCTION_PASSWORD, Resourcepart.from(AUCTION_RESOURCE))

    val chatManager = ChatManager.getInstanceFor(connection)
    chatManager.addChatListener { chat, _ ->
      currentChat = chat
      chat.addMessageListener(messageListener)
    }
    messageListener.drain()
  }

  private fun createAuctionItem(auctionId: AuctionId) =
    createUser("auction-$auctionId", AUCTION_PASSWORD)

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

  override suspend fun hasReceivedJoinRequestFrom(sniperId: BidderId) =
    hasReceivedMessage(sniperId) {
      it shouldBe "SOLVersion: 1.1; Command: JOIN;"
    }

  override suspend fun hasReceivedBid(bid: Int, sniperId: BidderId) = hasReceivedMessage(sniperId) {
    it shouldBe "SOLVersion: 1.1; Command: BID; Price: $bid;"
  }

  private fun hasReceivedMessage(
    sniperId: BidderId,
    matcher: (String?) -> Unit
  ) {
    messageListener.receivesAMessage(matcher)
    currentChat!!.participant shouldBe sniperId
  }

  override fun announceClosed() = currentChat!!.sendMessage("SOLVersion: 1.1; Event: CLOSE;")

  override fun close() {
    try {
      if (connection.isConnected) {
        messageListener.drain()
        connection.disconnect()
      }
    } catch (t: Throwable) {
      t.printStackTrace()
    }
  }

  override fun reportPrice(price: Int, increment: Int, bidder: BidderId) =
    currentChat!!.sendMessage(
      "SOLVersion: 1.1; Event: PRICE; CurrentPrice: $price; Increment: $increment; Bidder: $bidder;"
    )

  override fun sendInvalidMessageContaining(brokenMessage: String) {
    currentChat!!.sendMessage(brokenMessage)
  }

  companion object {
    const val AUCTION_RESOURCE = "Auction"
    const val XMPP_DOMAIN = "auctionhost.internal"
    const val AUCTION_PASSWORD = "auction"
  }
}

internal class SingleMessageListener : ChatMessageListener {

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
