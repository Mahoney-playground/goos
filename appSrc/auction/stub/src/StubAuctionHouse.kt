package goos.auction.stub

import goos.auction.api.Auction
import goos.auction.api.AuctionEventListener.PriceSource.FromOtherBidder
import goos.auction.api.AuctionEventListener.PriceSource.FromSniper
import goos.auction.api.AuctionHouse
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.maps.shouldContainKey
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

class StubAuctionHouse(
  private val sniperId: String
): AuctionHouse {

  private val connected: AtomicBoolean = AtomicBoolean(false)
  private val auctions = ConcurrentHashMap<String, StubGeneralAuction>()

  override fun auctionFor(itemId: String): Auction {
    connected.set(true)
    return StubAuction(itemId, sniperId, this)
  }

  override fun disconnect() {
    connected.set(false)
  }

  fun addAuction(itemId: String) {
    auctions[itemId] = StubGeneralAuction()
  }

  operator fun get(itemId: String): StubGeneralAuction = auctions.getValue(itemId)
}

class StubGeneralAuction {
  private val snipers = ConcurrentHashMap<String, StubAuction>()
  private val bids = CopyOnWriteArrayList<Pair<Int, String>>()

  fun addSniper(sniperAuction: StubAuction) {
    snipers[sniperAuction.sniperId] = sniperAuction
  }

  fun close() {
    snipers.values.forEach {
      it.auctionClosed()
    }
  }

  fun setPrice(price: Int, increment: Int, bidder: String) {
    snipers.values.forEach {
      it.currentPrice(
        price = price,
        increment = increment,
        source = if (it.sniperId == bidder) FromSniper else FromOtherBidder
      )
    }
  }

  fun hasSniper(sniperId: String) {
    snipers shouldContainKey sniperId
  }

  fun hasReceivedBid(bid: Int, bidder: String) {
    bids shouldContain bid to bidder
  }

  fun bid(bid: Int, bidder: String) {
    bids.add(bid to bidder)
  }

  fun sendInvalidMessage(brokenMessage: String) {
    snipers.values.forEach { it.auctionFailed() }
  }
}
