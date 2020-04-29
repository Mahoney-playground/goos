package goos.core

import org.jivesoftware.smack.chat.Chat
import org.jivesoftware.smack.chat.ChatMessageListener
import org.jivesoftware.smack.packet.Message

class AuctionMessageTranslator(
  private val listener: AuctionEventListener
) : ChatMessageListener {
  override fun processMessage(chat: Chat?, message: Message) {

    val event = unpackEventFrom(message)

    when (event["Event"]) {
      "CLOSE" -> { listener.auctionClosed() }
      "PRICE" -> { handleClose(event) }
    }
  }

  private fun unpackEventFrom(message: Message): Map<String, String> {
    return (message.body ?: "").toMap(
      pairDelimiter = ';',
      keyValueSeparator = ':'
    )
  }

  private fun handleClose(event: Map<String, String>) {
    val price = event.getValue("CurrentPrice").toInt()
    val increment = event.getValue("Increment").toInt()
    listener.currentPrice(price = price, increment = increment)
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
