package goos

import goos.ApplicationRunner.Companion.SNIPER_XMPP_ID
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.StringSpec
import kotlin.time.ExperimentalTime

@ExperimentalTime
class AuctionSniperEndToEndTest : StringSpec({

  val auction = FakeAuctionServer("item-54321")
  val application = ApplicationRunner()

  "sniper joins auction and loses" {

    auction.startSellingItem()

    application.startBiddingIn(auction)
    auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

    auction.reportPrice(1000, 98, "other bidder")
    application.hasShownSniperIsBidding()

    auction.hasReceivedBid(1098, SNIPER_XMPP_ID)

    auction.announceClosed()
    application.showSniperHasLostAuction()
  }

  "sniper wins an auction by bidding higher" {

    auction.startSellingItem()

    application.startBiddingIn(auction)
    auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

    auction.reportPrice(1000, 98, "other bidder")
    application.hasShownSniperIsBidding()

    auction.hasReceivedBid(1098, SNIPER_XMPP_ID)

    auction.reportPrice(1098, 97, SNIPER_XMPP_ID)
    application.hasShownSniperIsWinning()

    auction.announceClosed()
    application.showSniperHasWonAuction()
  }

  afterTest {
    auction.stop()
  }
}) {
  override fun isolationMode() = InstancePerTest
}
