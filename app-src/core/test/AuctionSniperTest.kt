package goos.core

import goos.auction.api.Auction
import goos.auction.api.AuctionEventListener.PriceSource.FromOtherBidder
import goos.auction.api.AuctionEventListener.PriceSource.FromSniper
import goos.core.SniperState.BIDDING
import goos.core.SniperState.FAILED
import goos.core.SniperState.LOSING
import goos.core.SniperState.LOST
import goos.core.SniperState.WINNING
import goos.core.SniperState.WON
import goos.ui.api.ItemId
import goos.ui.api.SniperListener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify

class AuctionSniperTest : StringSpec({

  val auction = mockk<Auction>(relaxed = true)
  val sniperListener = mockk<SniperListener>(relaxed = true)
  val item = ItemData(ITEM_ID, 10_000)
  val sniper = AuctionSniper(item, auction).apply {
    addSniperListener(sniperListener)
  }

  "reports lost if auction closes immediately" {
    sniper.auctionClosed()

    verify(exactly = 1) {
      sniperListener.sniperStateChanged(
        SniperSnapshot(
          item,
          0,
          0,
          LOST,
        ),
      )
    }
  }

  "reports lost if auction closes when bidding" {
    sniper.currentPrice(123, 45, FromOtherBidder)
    sniper.auctionClosed()

    verify(exactly = 1) {
      sniperListener.sniperStateChanged(
        SniperSnapshot(
          item,
          123,
          168,
          BIDDING,
        ),
      )
    }
    verify(exactly = 1) {
      sniperListener.sniperStateChanged(
        SniperSnapshot(
          item,
          123,
          168,
          LOST,
        ),
      )
    }
  }

  "reports won if auction closes when winning" {
    sniper.currentPrice(123, 45, FromSniper)
    sniper.auctionClosed()

    verify(exactly = 1) {
      sniperListener.sniperStateChanged(
        SniperSnapshot(
          item,
          123,
          0,
          WINNING,
        ),
      )
    }
    verify(exactly = 1) {
      sniperListener.sniperStateChanged(
        SniperSnapshot(
          item,
          123,
          0,
          WON,
        ),
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
          item,
          price,
          bid,
          BIDDING,
        ),
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
          item,
          price,
          0,
          WINNING,
        ),
      )
    }
  }

  "reports is losing when current price comes from other and next bid is higher than stop price" {

    val price = 1001
    val increment = 25

    val stopPriceItem = ItemData(ITEM_ID, (price + increment) - 1)
    val stopPriceSniper = AuctionSniper(
      stopPriceItem,
      auction = auction,
    ).apply {
      addSniperListener(sniperListener)
    }

    stopPriceSniper.currentPrice(price, increment, FromOtherBidder)

    confirmVerified(auction)
    verify(exactly = 1) {
      sniperListener.sniperStateChanged(
        SniperSnapshot(
          stopPriceItem,
          lastPrice = price,
          lastBid = 0,
          coreState = LOSING,
        ),
      )
    }
  }

  "reports failed if auction fails when bidding" {

    sniper.currentPrice(100, 10, FromOtherBidder)
    sniper.auctionFailed()

    verify(exactly = 1) {
      sniperListener.sniperStateChanged(
        SniperSnapshot(
          item,
          lastPrice = 0,
          lastBid = 0,
          coreState = FAILED,
        ),
      )
    }
  }
},) {
  override fun isolationMode() = IsolationMode.InstancePerTest

  companion object {
    val ITEM_ID = ItemId("1234")
  }
}
