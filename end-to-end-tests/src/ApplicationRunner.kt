package goos

import kotlin.time.ExperimentalTime

@ExperimentalTime
class ApplicationRunner(
  private val driver: AuctionSniperDriver = AuctionSniperDriver()
) {

  suspend fun startBiddingIn(
    auction: FakeAuctionServer
  ) {
    driver.joinAuction()
    driver.showSniperStatus(STATUS_JOINING)
    auction.itemId
  }

  suspend fun showSniperHasLostAuction() {
    driver.showSniperStatus(STATUS_LOST)
  }

  suspend fun hasShownSniperIsBidding() {
    driver.showSniperStatus(STATUS_BIDDING)
  }

  suspend fun hasShownSniperIsWinning() {
    driver.showSniperStatus(STATUS_WINNING)
  }

  suspend fun showSniperHasWonAuction() {
    driver.showSniperStatus(STATUS_WON)
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
