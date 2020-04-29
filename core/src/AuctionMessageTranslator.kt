package goos.core

import org.jivesoftware.smack.chat.Chat
import org.jivesoftware.smack.chat.ChatMessageListener
import org.jivesoftware.smack.packet.Message
import uk.org.lidalia.kotlinlangext.strings.toMap

class AuctionMessageTranslator(
  private val listener: AuctionEventListener
) : ChatMessageListener {
  override fun processMessage(chat: Chat?, message: Message) {

    val event = AuctionEvent.from(message.body ?: "")

    when (event.type) {
      "CLOSE" -> { listener.auctionClosed() }
      "PRICE" -> { listener.currentPrice(price = event.price, increment = event.increment) }
    }
  }
}

private class AuctionEvent(
  private val fields: Map<String, String>
) {

  val type = fields.getValue("Event")

  val price get() = getInt("CurrentPrice")
  val increment get() = getInt("Increment")

  private fun getInt(key: String) = fields.getValue(key).toInt()

  companion object {
    fun from(message: String): AuctionEvent = AuctionEvent(message.toMap(
      pairDelimiter = ';',
      keyValueSeparator = ':'
    ))
  }
}

interface AuctionEventListener {
  fun auctionClosed()
  fun currentPrice(price: Int, increment: Int)
}
