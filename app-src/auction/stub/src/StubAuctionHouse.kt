package goos.auction.stub

import goos.auction.api.Auction
import goos.auction.api.AuctionHouse
import goos.auction.sol.MessageTransport
import goos.auction.sol.SolAuction
import java.util.concurrent.atomic.AtomicBoolean

class StubAuctionHouse(
  private val sniperId: String,
  private val stubAuctionServer: StubAuctionServer
) : AuctionHouse {

  private val connected: AtomicBoolean = AtomicBoolean(false)

  override fun auctionFor(itemId: String): Auction {
    connected.set(true)
    return SolAuction(sniperId) { messageListener ->
      stubAuctionServer.subscribe(itemId, messageListener)
      StubMessageTransport(sniperId) { sniperId, message ->
        stubAuctionServer.receiveMessage(itemId, Message(sniperId, message))
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
