package goos

import goos.ApplicationRunner.Companion.SNIPER_XMPP_ID
import io.kotest.core.spec.style.StringSpec
import kotlin.time.ExperimentalTime

@ExperimentalTime
class AuctionSniperEndToEndTest : StringSpec({

  val auction = FakeAuctionServer("item-54321")
  val application = ApplicationRunner()

  "sniper joins auction until auction closes" {

    auction.startSellingItem()

    application.startBiddingIn(auction)
    auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

    auction.announceClosed()

    application.showSniperHasLostAuction()
  }

  afterTest {
    auction.stop()
  }
})
