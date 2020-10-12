package goos

import goos.ApplicationRunner.Companion.SNIPER_XMPP_ID
import goos.auction.api.AuctionDriver
import goos.auction.xmpp.XmppAuctionDriver
import goos.ui.swing.AuctionSniperDriver
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class AuctionSniperEndToEndTest : StringSpec({

  include(
    auctionSniperEndToEndTest(
      XmppAuctionDriver("item-54321"),
      XmppAuctionDriver("item-65432"),
      ApplicationRunner(AuctionSniperDriver())
    )
  )
}) {
  override fun isolationMode() = InstancePerTest
}

@ExperimentalTime
internal fun auctionSniperEndToEndTest(
  auctionDriver: AuctionDriver,
  auctionDriver2: AuctionDriver,
  application: ApplicationRunner
) = stringSpec {

  "basic appearance of the app" {
    application.hasCorrectAppearance()
  }

  "sniper joins auction, loses without bidding" {

    auctionDriver.startSellingItem()

    application.startBiddingIn(auctionDriver)
    auctionDriver.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

    auctionDriver.announceClosed()
    application.showSniperHasLostAuction(auctionDriver, lastPrice = 0, lastBid = 0)
  }

  "sniper joins auction, bids and loses" {

    auctionDriver.startSellingItem()

    application.startBiddingIn(auctionDriver)
    auctionDriver.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

    auctionDriver.reportPrice(price = 1_000, increment = 98, bidder = "other bidder")
    application.hasShownSniperIsBidding(auctionDriver, lastPrice = 1_000, lastBid = 1_098)
    auctionDriver.hasReceivedBid(bid = 1_098, sniperId = SNIPER_XMPP_ID)

    auctionDriver.announceClosed()
    application.showSniperHasLostAuction(auctionDriver, lastPrice = 1_000, lastBid = 1_098)
  }

  "sniper wins an auction by bidding higher" {

    auctionDriver.startSellingItem()

    application.startBiddingIn(auctionDriver)
    auctionDriver.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

    auctionDriver.reportPrice(price = 1_000, increment = 98, bidder = "other bidder")
    application.hasShownSniperIsBidding(auctionDriver, lastPrice = 1_000, lastBid = 1_098)
    auctionDriver.hasReceivedBid(bid = 1_098, sniperId = SNIPER_XMPP_ID)

    auctionDriver.reportPrice(price = 1_098, increment = 97, bidder = SNIPER_XMPP_ID)
    application.hasShownSniperIsWinning(auctionDriver, winningBid = 1_098)

    auctionDriver.announceClosed()
    application.showSniperHasWonAuction(auctionDriver, lastPrice = 1_098)
  }

  "sniper loses an auction after bidding" {

    auctionDriver.startSellingItem()

    application.startBiddingIn(auctionDriver)
    auctionDriver.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

    auctionDriver.reportPrice(price = 1_000, increment = 98, bidder = "other bidder")
    application.hasShownSniperIsBidding(auctionDriver, lastPrice = 1_000, lastBid = 1_098)

    auctionDriver.hasReceivedBid(bid = 1_098, sniperId = SNIPER_XMPP_ID)

    auctionDriver.reportPrice(price = 1_098, increment = 100, bidder = SNIPER_XMPP_ID)
    application.hasShownSniperIsWinning(auctionDriver, winningBid = 1_098)

    auctionDriver.reportPrice(price = 1_198, increment = 110, bidder = "other bidder")
    application.hasShownSniperIsBidding(auctionDriver, lastPrice = 1_198, lastBid = 1_308)
    auctionDriver.hasReceivedBid(bid = 1_308, sniperId = SNIPER_XMPP_ID)

    auctionDriver.announceClosed()
    application.showSniperHasLostAuction(auctionDriver, lastPrice = 1_198, lastBid = 1_308)
  }

  "sniper bids for multiple items" {

    auctionDriver.startSellingItem()
    auctionDriver2.startSellingItem()

    application.startBiddingIn(auctionDriver, auctionDriver2)
    auctionDriver.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)
    auctionDriver2.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

    auctionDriver.reportPrice(price = 1_000, increment = 98, bidder = "other bidder")
    auctionDriver.hasReceivedBid(bid = 1_098, sniperId = SNIPER_XMPP_ID)

    auctionDriver2.reportPrice(price = 500, increment = 21, bidder = "other bidder")
    auctionDriver2.hasReceivedBid(bid = 521, sniperId = SNIPER_XMPP_ID)

    auctionDriver.reportPrice(price = 1_098, increment = 97, bidder = SNIPER_XMPP_ID)
    auctionDriver2.reportPrice(price = 521, increment = 22, bidder = SNIPER_XMPP_ID)

    application.hasShownSniperIsWinning(auctionDriver, winningBid = 1_098)
    application.hasShownSniperIsWinning(auctionDriver2, winningBid = 521)

    auctionDriver.announceClosed()
    auctionDriver2.announceClosed()

    application.showSniperHasWonAuction(auctionDriver, lastPrice = 1_098)
    application.showSniperHasWonAuction(auctionDriver2, lastPrice = 521)
  }

  "sniper loses an auction when the price is too high" {

    auctionDriver.startSellingItem()
    application.startBiddingIn(auctionDriver, stopPrice = 1_100)
    auctionDriver.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

    auctionDriver.reportPrice(1000, 98, "other bidder")
    application.hasShownSniperIsBidding(auctionDriver, lastPrice = 1000, lastBid = 1_098)
    auctionDriver.hasReceivedBid(1098, SNIPER_XMPP_ID)

    auctionDriver.reportPrice(1197, 10, "third party")
    application.hasShownSniperIsLosing(auctionDriver, lastPrice = 1197, lastBid = 1_098)

    auctionDriver.reportPrice(1207, 10, "fourth party")
    application.hasShownSniperIsLosing(auctionDriver, lastPrice = 1207, lastBid = 1_098)

    auctionDriver.announceClosed()
    application.showSniperHasLostAuction(auctionDriver, lastPrice = 1_207, lastBid = 1_098)
  }

  "sniper reports invalid auction message and stops responding to events" {

    auctionDriver.startSellingItem()
    application.startBiddingIn(auctionDriver)

    auctionDriver.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

    auctionDriver.reportPrice(price = 500, increment = 20, bidder = "other bidder")
    auctionDriver.hasReceivedBid(bid = 520, sniperId = SNIPER_XMPP_ID)

    auctionDriver.sendInvalidMessageContaining("a broken message")
    application.showsSniperHasFailed(auctionDriver)

    auctionDriver.reportPrice(price = 520, increment = 21, bidder = "other")
    application.waitForAnotherAuctionEvent(auctionDriver2)
    application.showsSniperHasFailed(auctionDriver)
  }

  beforeTest { application.reset() }
  afterTest { auctionDriver.close() }
  afterTest { auctionDriver2.close() }
}

@ExperimentalTime
internal suspend fun ApplicationRunner.waitForAnotherAuctionEvent(
  auction2: AuctionDriver
) {
  auction2.startSellingItem()
  startBiddingIn(auction2)
  auction2.reportPrice(600, 6, "other")
  hasShownSniperIsBidding(auction2, 600, 606)
}
