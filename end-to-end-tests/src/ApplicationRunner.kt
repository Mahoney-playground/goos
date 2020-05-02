package goos

import kotlin.time.ExperimentalTime

@ExperimentalTime
class ApplicationRunner(
  private val driver: AuctionSniperDriver = AuctionSniperDriver()
) {

  private lateinit var itemId: String

  fun startBiddingIn(
    auction: FakeAuctionServer
  ) {
    driver.joinAuction()
    itemId = auction.itemId
    driver.showSniperState(itemId, 0, 0, STATE_JOINING)
  }

  fun showSniperHasLostAuction(lastPrice: Int, lastBid: Int) {
    driver.showSniperState(itemId, lastPrice, lastBid, STATE_LOST)
  }

  fun hasShownSniperIsBidding(lastPrice: Int, lastBid: Int) {
    driver.showSniperState(itemId, lastPrice, lastBid, STATE_BIDDING)
  }

  fun hasShownSniperIsWinning(winningBid: Int) {
    driver.showSniperState(itemId, winningBid, winningBid, STATE_WINNING)
  }

  fun showSniperHasWonAuction(lastPrice: Int) {
    driver.showSniperState(itemId, lastPrice, lastPrice, STATE_WON)
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
