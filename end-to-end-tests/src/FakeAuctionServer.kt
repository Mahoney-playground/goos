package goos

import io.kotest.matchers.shouldNotBe
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode.disabled
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.admin.ServiceAdministrationManager
import org.jxmpp.jid.impl.JidCreate
import uk.org.lidalia.retry.retry
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit.SECONDS

class FakeAuctionServer(
  val itemId: String
) {

  private val connection = XMPPTCPConnection(
    XMPPTCPConnectionConfiguration.builder()
      .setSecurityMode(disabled)
      .setXmppDomain(
        JidCreate.domainBareFrom(XMPP_DOMAIN)
      )
      .setHost(XMPP_HOSTNAME)
      .build()
  )

  private lateinit var currentChat: Chat

  private val messageListener = SingleMessageListener()

  fun startSellingItem() {

    retry { connection.connect() }

    createAuctionItem(itemId)
    connection.connect()
    connection.login("auction-$itemId", AUCTION_PASSWORD)

    ChatManager.getInstanceFor(connection).addIncomingListener { _, _, chat -> currentChat = chat }
  }

  private fun createAuctionItem(itemId: String) {
    connection.login("admin", "admin")
    ServiceAdministrationManager.getInstanceFor(connection).addUser(
      JidCreate.entityBareFrom("auction-$itemId@$XMPP_DOMAIN"),
      AUCTION_PASSWORD
    )
    connection.disconnect()
  }

  fun hasReceivedJoinRequestFromSniper() {
    messageListener.receivesAMessage()
  }

  fun announceClosed() {
    currentChat.send(Message())
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
    const val XMPP_DOMAIN = "auctionhost"
//    const val XMPP_HOSTNAME = "localhost"
    const val XMPP_HOSTNAME = XMPP_DOMAIN
    const val AUCTION_PASSWORD = "auction"
  }
}

class SingleMessageListener : MessageListener {

  private val messages: BlockingQueue<Message> = ArrayBlockingQueue(1)

  override fun processMessage(message: Message) {
    messages.add(message)
  }

  fun receivesAMessage() {
    messages.poll(5, SECONDS) shouldNotBe null
  }
}
