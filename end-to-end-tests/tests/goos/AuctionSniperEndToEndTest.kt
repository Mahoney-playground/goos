package goos

import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.specs.StringSpec
import kotlin.time.ExperimentalTime

@ExperimentalTime
class AuctionSniperEndToEndTest : StringSpec() {

  private val auction = FakeAuctionServer("item-54321")
  private val application = ApplicationRunner()

  init {

    "sniper joins auction until auction closes" {

      auction.startSellingItem()

      application.startBiddingIn(auction)

      auction.hasReceivedJoinRequestFromSniper()

      auction.announceClosed()

      application.showSniperHasLostAuction()
    }
  }

  override fun afterTest(testCase: TestCase, result: TestResult) {
    auction.stop()
    application.stop()
  }
}
