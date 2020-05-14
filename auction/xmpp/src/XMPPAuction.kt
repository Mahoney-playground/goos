package goos.auction.xmpp

import goos.auction.api.Auction
import goos.auction.api.AuctionEventListener
import goos.auction.api.MultiAuctionEventListener
import goos.auction.api.NoOpAuctionEventListener
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.chat.Chat
import org.jivesoftware.smack.chat.ChatManager
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart

internal class XMPPAuction(
  connection: XMPPConnection,
  itemId: String
) : Auction {

  private val auctionEventListeners = MultiAuctionEventListener()

  private val chat: Chat

  init {
    val translator = AuctionMessageTranslator(
      connection.user.toString(),
      auctionEventListeners
    )

    chat = ChatManager.getInstanceFor(connection)
      .createChat(
        auctionId(itemId, connection.host),
        translator
      )
    addAuctionEventListener(chatDisconnectorFor(chat, translator))
  }

  private fun chatDisconnectorFor(chat: Chat, translator: AuctionMessageTranslator) =
    object : NoOpAuctionEventListener {
      override fun auctionFailed() { chat.removeMessageListener(translator) }
    }

  override fun addAuctionEventListener(listener: AuctionEventListener) {
    auctionEventListeners.addListener(listener)
  }

  override fun join() {
    chat.sendMessage("SOLVersion: 1.1; Command: JOIN;")
  }

  override fun bid(bid: Int) {
    chat.sendMessage("SOLVersion: 1.1; Command: BID; Price: $bid;")
  }

  companion object {

    private val AUCTION_RESOURCE = Resourcepart.from("Auction")

    private fun auctionId(itemId: String, hostname: String): EntityBareJid =
      JidCreate.entityBareFrom("auction-$itemId@$hostname/$AUCTION_RESOURCE")
  }
}
