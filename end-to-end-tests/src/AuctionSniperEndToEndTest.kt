package goos

import goos.ApplicationRunner.Companion.SNIPER_XMPP_ID
import goos.xmpptestsupport.FakeAuctionServer
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.StringSpec
import kotlin.time.ExperimentalTime

@ExperimentalTime
class AuctionSniperEndToEndTest : StringSpec({

  val auction = FakeAuctionServer("item-54321")
  val auction2 = FakeAuctionServer("item-65432")
  val application = ApplicationRunner()

  "sniper joins auction, loses without bidding" {

    auction.startSellingItem()

    application.startBiddingIn(auction)
    auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

    auction.announceClosed()
    application.showSniperHasLostAuction(auction, lastPrice = 0, lastBid = 0)
  }

  "sniper joins auction, bids and loses" {

    auction.startSellingItem()

    application.startBiddingIn(auction)
    auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

    auction.reportPrice(price = 1_000, increment = 98, bidder = "other bidder")
    application.hasShownSniperIsBidding(auction, lastPrice = 1_000, lastBid = 1_098)
    auction.hasReceivedBid(bid = 1_098, sniperId = SNIPER_XMPP_ID)

    auction.announceClosed()
    application.showSniperHasLostAuction(auction, lastPrice = 1_000, lastBid = 1_098)
  }

  "sniper wins an auction by bidding higher" {

    auction.startSellingItem()

    application.startBiddingIn(auction)
    auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

    auction.reportPrice(price = 1_000, increment = 98, bidder = "other bidder")
    application.hasShownSniperIsBidding(auction, lastPrice = 1_000, lastBid = 1_098)
    auction.hasReceivedBid(bid = 1_098, sniperId = SNIPER_XMPP_ID)

    auction.reportPrice(price = 1_098, increment = 97, bidder = SNIPER_XMPP_ID)
    application.hasShownSniperIsWinning(auction, winningBid = 1_098)

    auction.announceClosed()
    application.showSniperHasWonAuction(auction, lastPrice = 1_098)
  }

  "sniper loses an auction after bidding" {

    auction.startSellingItem()

    application.startBiddingIn(auction)
    auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

    auction.reportPrice(price = 1_000, increment = 98, bidder = "other bidder")
    application.hasShownSniperIsBidding(auction, lastPrice = 1_000, lastBid = 1_098)

    auction.hasReceivedBid(bid = 1_098, sniperId = SNIPER_XMPP_ID)

    auction.reportPrice(price = 1_098, increment = 100, bidder = SNIPER_XMPP_ID)
    application.hasShownSniperIsWinning(auction, winningBid = 1_098)

    auction.reportPrice(price = 1_198, increment = 110, bidder = "other bidder")
    application.hasShownSniperIsBidding(auction, lastPrice = 1_198, lastBid = 1_308)
    auction.hasReceivedBid(bid = 1_308, sniperId = SNIPER_XMPP_ID)

    auction.announceClosed()
    application.showSniperHasLostAuction(auction, lastPrice = 1_198, lastBid = 1_308)
  }

  "sniper bids for multiple items" {

    auction.startSellingItem()
    auction2.startSellingItem()

    application.startBiddingIn(auction, auction2)
    auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)
    auction2.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

    auction.reportPrice(price = 1_000, increment = 98, bidder = "other bidder")
    auction.hasReceivedBid(bid = 1_098, sniperId = SNIPER_XMPP_ID)

    auction2.reportPrice(price = 500, increment = 21, bidder = "other bidder")
    auction2.hasReceivedBid(bid = 521, sniperId = SNIPER_XMPP_ID)

    auction.reportPrice(price = 1_098, increment = 97, bidder = SNIPER_XMPP_ID)
    auction2.reportPrice(price = 521, increment = 22, bidder = SNIPER_XMPP_ID)

    application.hasShownSniperIsWinning(auction, winningBid = 1_098)
    application.hasShownSniperIsWinning(auction2, winningBid = 521)

    auction.announceClosed()
    auction2.announceClosed()

    application.showSniperHasWonAuction(auction, lastPrice = 1_098)
    application.showSniperHasWonAuction(auction2, lastPrice = 521)
  }

  "sniper loses an auction when the price is too high" {

    auction.startSellingItem()
    application.startBiddingIn(auction, stopPrice = 1_100)
    auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

    auction.reportPrice(1000, 98, "other bidder")
    application.hasShownSniperIsBidding(auction, lastPrice = 1000, lastBid = 1_098)
    auction.hasReceivedBid(1098, SNIPER_XMPP_ID)

    auction.reportPrice(1197, 10, "third party")
    application.hasShownSniperIsLosing(auction, lastPrice = 1197, lastBid = 1_098)

    auction.reportPrice(1207, 10, "fourth party")
    application.hasShownSniperIsLosing(auction, lastPrice = 1207, lastBid = 1_098)

    auction.announceClosed()
    application.showSniperHasLostAuction(auction, lastPrice = 1_207, lastBid = 1_098)
  }

  "sniper reports invalid auction message and stops responding to events" {

    auction.startSellingItem()
    application.startBiddingIn(auction)

    auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

    auction.reportPrice(price = 500, increment = 20, bidder = "other bidder")
    auction.hasReceivedBid(bid = 520, sniperId = SNIPER_XMPP_ID)

    auction.sendInvalidMessageContaining("a broken message")
    application.showsSniperHasFailed(auction)

    auction.reportPrice(price = 520, increment = 21, bidder = "other")
    application.waitForAnotherAuctionEvent(auction2)

//    application.reportsInvalidMessage(auction, "a broken message")
    application.showsSniperHasFailed(auction)
  }

  beforeTest { application.reset() }
  afterTest { auction.stop() }
  afterTest { auction2.stop() }
}) {
  override fun isolationMode() = InstancePerTest
}

@ExperimentalTime
fun ApplicationRunner.waitForAnotherAuctionEvent(
  auction2: FakeAuctionServer
) {
  auction2.startSellingItem()
  startBiddingIn(auction2)
  auction2.reportPrice(600, 6, "other")
  hasShownSniperIsBidding(auction2, 600, 606)
}
