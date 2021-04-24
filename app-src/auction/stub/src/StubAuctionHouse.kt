package goos.auction.stub

import goos.auction.api.Auction
import goos.auction.api.AuctionHouse
import goos.auction.api.AuctionId
import goos.auction.api.BidderId
import goos.auction.sol.MessageTransport
import goos.auction.sol.SolAuction
import java.util.concurrent.atomic.AtomicBoolean

class StubAuctionHouse(
  private val sniperId: BidderId,
  private val stubAuctionServer: StubAuctionServer
) : AuctionHouse {

  private val connected: AtomicBoolean = AtomicBoolean(false)

  override fun auctionFor(auctionId: AuctionId): Auction {
    connected.set(true)
    return SolAuction(sniperId) { messageListener ->
      stubAuctionServer.subscribe(auctionId, messageListener)
      StubMessageTransport(sniperId) { sniperId, message ->
        stubAuctionServer.receiveMessage(auctionId, Message(sniperId, message))
      }
    }
  }

  override fun disconnect() {
    connected.set(false)
  }
}

class StubMessageTransport(
  private val sniperId: BidderId,
  private val messageSink: (BidderId, String) -> Unit
) : MessageTransport {
  override fun sendMessage(message: String) {
    messageSink(sniperId, message)
  }
}
