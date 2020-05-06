package goos.auction.api

interface Auction {

  fun addAuctionEventListener(listener: AuctionEventListener)

  fun join()

  fun bid(bid: Int)
}
