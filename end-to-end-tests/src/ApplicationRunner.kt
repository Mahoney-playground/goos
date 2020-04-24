package goos

import io.kotest.assertions.timing.eventually
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
class ApplicationRunner(
  private val driver: AuctionSniperDriver = AuctionSniperDriver()
) {

  suspend fun startBiddingIn(
    auction: FakeAuctionServer
  ) {
    driver.joinAuction()

    eventually(5.seconds) {
      driver.showSniperStatus(STATUS_JOINING)
    }
    auction.itemId
  }

  fun showSniperHasLostAuction() {
    driver.showSniperStatus(STATUS_LOST)
  }

  companion object {
    const val STATUS_LOST: String = "Lost"
    const val STATUS_JOINING: String = "Joining"
  }
}
