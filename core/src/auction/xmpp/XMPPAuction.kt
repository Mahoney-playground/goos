package goos.core.auction.xmpp

import goos.core.auction.api.Auction
import org.jivesoftware.smack.chat.Chat

class XMPPAuction(
  private val chat: Chat
) : Auction {
  override fun bid(bid: Int) {
    chat.sendMessage("SOLVersion: 1.1; Command: BID; Price: $bid;")
  }

  fun join() {
    chat.sendMessage("SOLVersion: 1.1; Command: JOIN;")
  }
}
