package goos.auction.stub

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class StubAuctionServer {

  val liveAuctions: ConcurrentMap<String, StubAuctionBroker> = ConcurrentHashMap()
  val closedAuctions: ConcurrentMap<String, StubAuctionBroker> = ConcurrentHashMap()

  fun startAuction(itemId: String) {
    liveAuctions[itemId] = StubAuctionBroker()
  }

  fun close(itemId: String) {
    sendToSubscribers(itemId, "SOLVersion: 1.1; Event: CLOSE;")
    liveAuctions.remove(itemId)?.let { auction ->
      closedAuctions[itemId] = auction
    }
  }

  fun sendToSubscribers(itemId: String, message: String) {
    liveAuctions[itemId]?.sendAuctionServerMessage(message)
  }

  fun allAuctions() = liveAuctions + closedAuctions
  fun reset() {
    liveAuctions.clear()
    closedAuctions.clear()
  }
}
