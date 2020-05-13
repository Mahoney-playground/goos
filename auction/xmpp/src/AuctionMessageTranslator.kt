package goos.auction.xmpp

import goos.auction.api.AuctionEventListener
import goos.auction.api.AuctionEventListener.PriceSource
import goos.auction.api.AuctionEventListener.PriceSource.FromOtherBidder
import goos.auction.api.AuctionEventListener.PriceSource.FromSniper
import org.jivesoftware.smack.chat.Chat
import org.jivesoftware.smack.chat.ChatMessageListener
import org.jivesoftware.smack.packet.Message
import uk.org.lidalia.kotlinlangext.strings.toMap

internal class AuctionMessageTranslator(
  private val sniperId: String,
  private val listener: AuctionEventListener
) : ChatMessageListener {
  override fun processMessage(chat: Chat?, message: Message) {

    val event = AuctionEvent.from(message.body ?: "")

    when (event.type) {
      "CLOSE" -> listener.auctionClosed()
      "PRICE" -> listener.currentPrice(
        price = event.price,
        increment = event.increment,
        source = event.isFrom(sniperId)
      )
    }
  }
}

private class AuctionEvent(
  private val fields: Map<String, String>
) {

  val type: String = fields.getValue("Event")

  val price: Int get() = getInt("CurrentPrice")
  val increment: Int get() = getInt("Increment")
  private val bidder: String get() = fields.getValue("Bidder")

  fun isFrom(sniperId: String): PriceSource =
    if (bidder == sniperId) FromSniper else FromOtherBidder

  private fun getInt(key: String) = fields.getValue(key).toInt()

  companion object {
    fun from(message: String): AuctionEvent = AuctionEvent(message.toMap(
      pairDelimiter = ';',
      keyValueSeparator = ':'
    ))
  }
}
