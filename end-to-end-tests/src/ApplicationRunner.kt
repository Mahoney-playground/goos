package goos

class ApplicationRunner {

  private var driver: AuctionSniperDriver? = null

  fun startBiddingIn(
    auction: FakeAuctionServer
  ) {

    driver = AuctionSniperDriver()

    driver!!.showSniperStatus(STATUS_JOINING)
    auction.itemId
  }

  fun showSniperHasLostAuction() {
    driver!!.showSniperStatus(STATUS_LOST)
  }

  companion object {
    const val SNIPER_ID = "sniper"
    const val SNIPER_PASSWORD = "sniper"
    const val STATUS_LOST: String = "Lost"
    const val STATUS_JOINING: String = "Joining"
  }
}
