package goos

import goos.ApplicationRunner.Companion.SNIPER_XMPP_ID
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.StringSpec
import kotlin.time.ExperimentalTime

@ExperimentalTime
class AuctionSniperEndToEndTest : StringSpec({

  val auction = FakeAuctionServer("item-54321")
  val application = ApplicationRunner()

  "sniper joins auction, loses without bidding" {

    auction.startSellingItem()

    application.startBiddingIn(auction)
    auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

    auction.announceClosed()
    application.showSniperHasLostAuction(lastPrice = 0)
  }

  "sniper joins auction, bids and loses" {

    auction.startSellingItem()

    application.startBiddingIn(auction)
    auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

    auction.reportPrice(price = 1_000, increment = 98, bidder = "other bidder")
    application.hasShownSniperIsBidding(lastPrice = 1_000, lastBid = 1_098)
    auction.hasReceivedBid(bid = 1_098, sniperId = SNIPER_XMPP_ID)

    auction.announceClosed()
    application.showSniperHasLostAuction(lastPrice = 1_098)
  }

  "sniper wins an auction by bidding higher" {

    auction.startSellingItem()

    application.startBiddingIn(auction)
    auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

    auction.reportPrice(price = 1_000, increment = 98, bidder = "other bidder")
    application.hasShownSniperIsBidding(lastPrice = 1_000, lastBid = 1_098)
    auction.hasReceivedBid(bid = 1_098, sniperId = SNIPER_XMPP_ID)

    auction.reportPrice(price = 1_098, increment = 97, bidder = SNIPER_XMPP_ID)
    application.hasShownSniperIsWinning(winningBid = 1_098)

    auction.announceClosed()
    application.showSniperHasWonAuction(lastPrice = 1_098)
  }

  "sniper loses an auction after bidding" {

    auction.startSellingItem()

    application.startBiddingIn(auction)
    auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

    auction.reportPrice(price = 1_000, increment = 98, bidder = "other bidder")
    application.hasShownSniperIsBidding(lastPrice = 1_000, lastBid = 1_098)

    auction.hasReceivedBid(bid = 1_098, sniperId = SNIPER_XMPP_ID)

    auction.reportPrice(price = 1_098, increment = 100, bidder = SNIPER_XMPP_ID)
    application.hasShownSniperIsWinning(winningBid = 1_098)

    auction.reportPrice(price = 1_198, increment = 110, bidder = "other bidder")
    application.hasShownSniperIsBidding(lastPrice = 1_198, lastBid = 1_308)
    auction.hasReceivedBid(bid = 1_308, sniperId = SNIPER_XMPP_ID)

    auction.announceClosed()
    application.showSniperHasLostAuction(lastPrice = 1_198)
  }

  afterTest {
    auction.stop()
  }
}) {
  override fun isolationMode() = InstancePerTest
}
