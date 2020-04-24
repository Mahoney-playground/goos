package goos

import io.kotest.matchers.shouldNotBe
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
import java.util.concurrent.TimeUnit.MINUTES

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
  }

  private fun createAuctionItem(itemId: String) {
    createUser("auction-$itemId", AUCTION_PASSWORD)
  }

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

  fun hasReceivedJoinRequestFromSniper() {
    messageListener.receivesAMessage()
  }

  fun announceClosed() {
    currentChat?.sendMessage(Message())
  }

  fun stop() {
    try {
      connection.disconnect()
    } catch (t: Throwable) {
      t.printStackTrace()
    }
  }

  companion object {
    const val AUCTION_RESOURCE = "Auction"
    const val XMPP_DOMAIN = "auctionhost.internal"
    const val AUCTION_PASSWORD = "auction"
  }
}

class SingleMessageListener : ChatMessageListener {

  private val messages: BlockingQueue<Message> = ArrayBlockingQueue(1)

  override fun processMessage(chat: Chat, message: Message) {
    messages.add(message)
  }

  fun receivesAMessage() {
    messages.poll(10, MINUTES) shouldNotBe null
  }
}
