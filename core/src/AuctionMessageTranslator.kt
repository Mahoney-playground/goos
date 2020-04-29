package goos.core

import org.jivesoftware.smack.chat.Chat
import org.jivesoftware.smack.chat.ChatMessageListener
import org.jivesoftware.smack.packet.Message

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

private fun String.toMap(
  pairDelimiter: Char,
  keyValueSeparator: Char
): Map<String, String> {
  return split(pairDelimiter)
    .map { it.split(keyValueSeparator, limit = 2) }
    .toMap()
}

private fun List<List<String>>.toMap(): Map<String, String> {
  return mapNotNull { it.toPair() }.toMap()
}

private fun List<String>.toPair(): Pair<String, String>? =
  if (this.size >= 2)
    this[0].trim() to this[1].trim()
  else null

interface AuctionEventListener {
  fun auctionClosed()
  fun currentPrice(price: Int, increment: Int)
}
