package goos.core

import goos.auction.api.Auction
import goos.auction.api.AuctionEventListener.PriceSource.FromOtherBidder
import goos.auction.api.AuctionEventListener.PriceSource.FromSniper
import goos.core.api.SniperState.BIDDING
import goos.core.api.SniperState.LOST
import goos.core.api.SniperState.WINNING
import goos.core.api.SniperState.WON
import goos.core.api.SniperListener
import goos.core.api.SniperSnapshot
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify

class AuctionSniperTest : StringSpec({

  val auction = mockk<Auction>(relaxed = true)
  val sniperListener = mockk<SniperListener>(relaxed = true)
  val sniper = AuctionSniper(ITEM_ID, auction).apply {
    addSniperListener(sniperListener)
  }

  "reports lost if auction closes immediately" {
    sniper.auctionClosed()

    verify(exactly = 1) {
      sniperListener.sniperStateChanged(
        SniperSnapshot(
          ITEM_ID,
          0,
          0,
          LOST
        )
      )
    }
  }

  "reports lost if auction closes when bidding" {
    sniper.currentPrice(123, 45, FromOtherBidder)
    sniper.auctionClosed()

    verify(exactly = 1) {
      sniperListener.sniperStateChanged(
        SniperSnapshot(
          ITEM_ID,
          123,
          168,
          BIDDING
        )
      )
    }
    verify(exactly = 1) {
      sniperListener.sniperStateChanged(
        SniperSnapshot(
          ITEM_ID,
          123,
          168,
          LOST
        )
      )
    }
  }

  "reports won if auction closes when winning" {
    sniper.currentPrice(123, 45, FromSniper)
    sniper.auctionClosed()

    verify(exactly = 1) {
      sniperListener.sniperStateChanged(
        SniperSnapshot(
          ITEM_ID,
          123,
          0,
          WINNING
        )
      )
    }
    verify(exactly = 1) {
      sniperListener.sniperStateChanged(
        SniperSnapshot(
          ITEM_ID,
          123,
          0,
          WON
        )
      )
    }
  }

  "bids higher and reports bidding when new price arrives" {

    val price = 1001
    val increment = 25
    val bid = price + increment

    sniper.currentPrice(price, increment, FromOtherBidder)

    verify(exactly = 1) { auction.bid(bid) }
    verify(exactly = 1) {
      sniperListener.sniperStateChanged(
        SniperSnapshot(
          ITEM_ID,
          price,
          bid,
          BIDDING
        )
      )
    }
  }

  "reports is winning when current price comes from sniper" {

    val price = 1001
    val increment = 25

    sniper.currentPrice(price, increment, FromSniper)

    confirmVerified(auction)
    verify(exactly = 1) {
      sniperListener.sniperStateChanged(
        SniperSnapshot(
          ITEM_ID,
          price,
          0,
          WINNING
        )
      )
    }
  }
}) {
  override fun isolationMode() = IsolationMode.InstancePerTest

  companion object {
    const val ITEM_ID = "1234"
  }
}
