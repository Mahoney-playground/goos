package goos.auction.stub

import goos.auction.api.Auction
import goos.auction.api.AuctionEventListener
import goos.auction.api.MultiAuctionEventListener

class StubAuction(
  val itemId: String,
  val sniperId: String,
  private val stubAuctionHouse: StubAuctionHouse,
  private val auctionEventListener: MultiAuctionEventListener = MultiAuctionEventListener()
) : Auction, AuctionEventListener by auctionEventListener {

  override fun addAuctionEventListener(listener: AuctionEventListener) {
    auctionEventListener.addListener(listener)
  }

  override fun join() {
    stubAuctionHouse[itemId].addSniper(this)
  }

  override fun bid(bid: Int) {
    stubAuctionHouse[itemId].bid(bid, sniperId)
  }
}
