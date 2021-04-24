package goos.auction.api

interface AuctionHouse {
  fun auctionFor(auctionId: AuctionId): Auction
  fun disconnect()
}
