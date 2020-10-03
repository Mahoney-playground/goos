package goos.auction.stub

import goos.auction.api.Auction
import goos.auction.api.AuctionHouse
import goos.auction.sol.MessageListener
import goos.auction.sol.MessageTransport
import goos.auction.sol.SolAuction
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

class StubAuctionHouse(
  private val sniperId: String,
  private val messageSink: (String, String) -> Unit,
  private val subscribers: (MessageListener) -> Unit
) : AuctionHouse {

  private val connected: AtomicBoolean = AtomicBoolean(false)

  private val auctions = ConcurrentHashMap<String, SolAuction>()

  override fun auctionFor(itemId: String): Auction {
    connected.set(true)
    return auctions.computeIfAbsent(itemId) {
      SolAuction(itemId) { messageListener ->
        subscribers(messageListener)
        StubMessageTransport(sniperId, messageSink)
      }
    }
  }

  override fun disconnect() {
    connected.set(false)
  }
}

class StubMessageTransport(
  private val sniperId: String,
  private val messageSink: (String, String) -> Unit
) : MessageTransport {
  override fun sendMessage(message: String) {
    messageSink(sniperId, message)
  }
}
