package goos.xmpptestsupport

import goos.auction.stub.StubAuctionHouse

class StubAuctionDriver(
  override val itemId: String,
  private val stubAuctionHouse: StubAuctionHouse
) : AuctionDriver {

  override fun startSellingItem() {
    stubAuctionHouse.addAuction(itemId)
  }

  override fun announceClosed() {
    stubAuctionHouse[itemId].close()
  }

  override fun reportPrice(price: Int, increment: Int, bidder: String) {
    stubAuctionHouse[itemId].setPrice(price, increment, bidder)
  }

  override fun sendInvalidMessageContaining(brokenMessage: String) {
    stubAuctionHouse[itemId].sendInvalidMessage(brokenMessage)
  }

  override fun hasReceivedJoinRequestFrom(sniperId: String) {
    stubAuctionHouse[itemId].hasSniper(sniperId)
  }

  override fun hasReceivedBid(bid: Int, sniperId: String) {
    stubAuctionHouse[itemId].hasReceivedBid(bid, sniperId)
  }

  override fun close() {}
}
