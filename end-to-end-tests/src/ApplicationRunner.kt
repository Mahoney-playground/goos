package goos

import kotlin.time.ExperimentalTime

@ExperimentalTime
class ApplicationRunner(
  private val driver: AuctionSniperDriver = AuctionSniperDriver()
) {

  private var itemId: String? = null

  fun startBiddingIn(
    auction: FakeAuctionServer
  ) {
    driver.joinAuction()
    itemId = auction.itemId
    driver.showSniperStatus(itemId!!, 0, 0, STATUS_JOINING)
  }

  fun showSniperHasLostAuction(lastPrice: Int) {
    driver.showSniperStatus(itemId!!, lastPrice, lastPrice, STATUS_LOST)
  }

  fun hasShownSniperIsBidding(lastPrice: Int, lastBid: Int) {
    driver.showSniperStatus(itemId!!, lastPrice, lastBid, STATUS_BIDDING)
  }

  fun hasShownSniperIsWinning(winningBid: Int) {
    driver.showSniperStatus(itemId!!, winningBid, winningBid, STATUS_WINNING)
  }

  fun showSniperHasWonAuction(lastPrice: Int) {
    driver.showSniperStatus(itemId!!, lastPrice, lastPrice, STATUS_WON)
  }

  companion object {
    const val SNIPER_XMPP_ID: String = "sniper@auctionhost.internal/Auction"
    const val STATUS_LOST: String = "Lost"
    const val STATUS_BIDDING: String = "Bidding"
    const val STATUS_JOINING: String = "Joining"
    const val STATUS_WINNING: String = "Winning"
    const val STATUS_WON: String = "Won"
  }
}
