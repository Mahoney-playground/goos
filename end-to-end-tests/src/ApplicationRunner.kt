package goos

import goos.uitestsupport.AuctionSniperDriver
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ApplicationRunner(
  private val driver: AuctionSniperDriver = AuctionSniperDriver()
) {

  init {
    driver.hasTitle("Auction Sniper")
    driver.hasColumnTitles()
  }

  fun startBiddingIn(
    vararg auctions: FakeAuctionServer
  ) {
    auctions.forEach { auction ->
      driver.startBiddingFor(auction.itemId)
      driver.showSniperState(auction.itemId, 0, 0, STATE_JOINING)
    }
  }

  fun showSniperHasLostAuction(auction: FakeAuctionServer, lastPrice: Int, lastBid: Int) {
    driver.showSniperState(auction.itemId, lastPrice, lastBid, STATE_LOST)
  }

  fun hasShownSniperIsBidding(auction: FakeAuctionServer, lastPrice: Int, lastBid: Int) {
    driver.showSniperState(auction.itemId, lastPrice, lastBid, STATE_BIDDING)
  }

  fun hasShownSniperIsWinning(auction: FakeAuctionServer, winningBid: Int) {
    driver.showSniperState(auction.itemId, winningBid, winningBid, STATE_WINNING)
  }

  fun showSniperHasWonAuction(auction: FakeAuctionServer, lastPrice: Int) {
    driver.showSniperState(auction.itemId, lastPrice, lastPrice, STATE_WON)
  }

  fun reset() {
    driver.clickReset()
  }

  companion object {
    const val SNIPER_XMPP_ID: String = "sniper@auctionhost.internal/Auction"
    const val STATE_LOST: String = "Lost"
    const val STATE_BIDDING: String = "Bidding"
    const val STATE_JOINING: String = "Joining"
    const val STATE_WINNING: String = "Winning"
    const val STATE_WON: String = "Won"
  }
}
