package goos.auction.xmpp

import goos.auction.api.Auction
import goos.auction.api.MultiAuctionEventListener
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.chat.ChatManager
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart

class XMPPAuction(
  connection: XMPPConnection,
  itemId: String,
  auctionEventListeners: MultiAuctionEventListener
) : Auction {

  private val chat = ChatManager.getInstanceFor(connection)
    .createChat(
      auctionId(itemId, connection.host),
      null
    )

  init {
    chat.addMessageListener(
      AuctionMessageTranslator(
        connection.user.toString(),
        auctionEventListeners
      )
    )
  }

  override fun bid(bid: Int) {
    chat.sendMessage("SOLVersion: 1.1; Command: BID; Price: $bid;")
  }

  fun join() {
    chat.sendMessage("SOLVersion: 1.1; Command: JOIN;")
  }

  companion object {

    private val AUCTION_RESOURCE = Resourcepart.from("Auction")

    private fun auctionId(itemId: String, hostname: String): EntityBareJid =
      JidCreate.entityBareFrom("auction-$itemId@$hostname/$AUCTION_RESOURCE")
  }
}
