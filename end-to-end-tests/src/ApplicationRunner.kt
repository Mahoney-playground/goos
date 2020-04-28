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

  companion object {
    const val SNIPER_XMPP_ID: String = "sniper@auctionhost.internal/Auction"
    const val STATUS_LOST: String = "Lost"
    const val STATUS_JOINING: String = "Joining"
  }
}
