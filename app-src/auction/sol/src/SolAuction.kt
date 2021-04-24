package goos.auction.sol

import goos.auction.api.Auction
import goos.auction.api.AuctionEventListener
import goos.auction.api.BidderId
import goos.auction.api.MultiAuctionEventListener
import goos.auction.api.NoOpAuctionEventListener

class SolAuction(
  sniperId: BidderId,
  transportFactory: (MessageListener) -> MessageTransport
) : Auction {

  private val auctionEventListeners = MultiAuctionEventListener()
  private val auctionMessageTranslator = AuctionMessageTranslator(
    sniperId,
    auctionEventListeners
  )

  private val transport = transportFactory(object : MessageListener {
    override fun processMessage(body: String?) {
      auctionMessageTranslator.processMessage(body)
    }
  })

  init {
    addAuctionEventListener(disconnectOnFailure())
  }

  private fun disconnectOnFailure() =
    object : NoOpAuctionEventListener {
      override fun auctionFailed() = auctionEventListeners.clear()
    }

  override fun addAuctionEventListener(listener: AuctionEventListener) {
    auctionEventListeners.addListener(listener)
  }

  override fun join() {
    transport.sendMessage("SOLVersion: 1.1; Command: JOIN;")
  }

  override fun bid(bid: Int) {
    transport.sendMessage("SOLVersion: 1.1; Command: BID; Price: $bid;")
  }
}

interface MessageTransport {
  fun sendMessage(message: String)
}

interface MessageListener {
  fun processMessage(body: String?)
}
