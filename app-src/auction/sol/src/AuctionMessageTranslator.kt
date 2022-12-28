package goos.auction.sol

import goos.auction.api.AuctionEventListener
import goos.auction.api.AuctionEventListener.PriceSource
import goos.auction.api.AuctionEventListener.PriceSource.FromOtherBidder
import goos.auction.api.AuctionEventListener.PriceSource.FromSniper
import goos.auction.api.BidderId
import goos.auction.api.toBidderId
import uk.org.lidalia.kotlinlangext.strings.toMap

internal class AuctionMessageTranslator(
  private val sniperId: BidderId,
  private val listener: AuctionEventListener,
) {
  fun processMessage(messageText: String?) {
    try {
      val event = AuctionEvent.from(messageText ?: "")

      when (event.type) {
        "CLOSE" -> listener.auctionClosed()
        "PRICE" -> listener.currentPrice(
          price = event.price,
          increment = event.increment,
          source = event.isFrom(sniperId),
        )
      }
    } catch (e: Exception) {
      listener.auctionFailed()
    }
  }
}

private class AuctionEvent(
  private val fields: Map<String, String>,
) {

  val type: String = fields.getValue("Event")

  val price: Int get() = getInt("CurrentPrice")
  val increment: Int get() = getInt("Increment")
  private val bidder: BidderId get() = fields.getValue("Bidder").toBidderId()

  fun isFrom(sniperId: BidderId): PriceSource =
    if (bidder == sniperId) FromSniper else FromOtherBidder

  private fun getInt(key: String) = fields.getValue(key).toInt()

  companion object {
    fun from(message: String): AuctionEvent = AuctionEvent(
      message.toMap(
        pairDelimiter = ';',
        keyValueSeparator = ':',
      ),
    )
  }
}
