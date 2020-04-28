package goos.core

import org.jivesoftware.smack.chat.Chat
import org.jivesoftware.smack.chat.ChatMessageListener
import org.jivesoftware.smack.packet.Message

class AuctionMessageTranslator(
  private val listener: AuctionEventListener
) : ChatMessageListener {
  override fun processMessage(chat: Chat?, message: Message?) {
    listener.auctionClosed()
  }
}

interface AuctionEventListener {
  fun auctionClosed()
}
