package goos.core

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
