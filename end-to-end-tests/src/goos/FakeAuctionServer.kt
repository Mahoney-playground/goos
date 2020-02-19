package goos

import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.tcp.XMPPTCPConnection

class FakeAuctionServer(
  val itemId: String
) {

  private val connection = XMPPTCPConnection(XMPP_HOSTNAME, "auction-$itemId", AUCTION_PASSWORD)
  private var currentChat: Chat? = null

  fun startSellingItem() {

    connection.connect()
    connection.login("auction-$itemId", AUCTION_PASSWORD)

    ChatManager.getInstanceFor(connection).addIncomingListener { _, _, chat -> currentChat = chat }
  }

  fun hasReceivedJoinRequestFromSniper() {
    TODO("not implemented")
  }

  fun announceClosed() {
    TODO("not implemented")
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
