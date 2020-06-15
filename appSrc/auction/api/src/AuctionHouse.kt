package goos.auction.api

interface AuctionHouse {
  fun auctionFor(itemId: String): Auction
  fun disconnect()
}
