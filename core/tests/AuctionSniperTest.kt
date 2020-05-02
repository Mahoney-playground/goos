package goos.core

import goos.core.AuctionEventListener.PriceSource.FromOtherBidder
import goos.core.AuctionEventListener.PriceSource.FromSniper
import goos.core.SniperState.BIDDING
import goos.core.SniperState.LOST
import goos.core.SniperState.WINNING
import goos.core.SniperState.WON
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.mockk.Called
import io.mockk.mockk
import io.mockk.verify

class AuctionSniperTest : StringSpec({

  val auction = mockk<Auction>(relaxed = true)
  val sniperListener = mockk<SniperListener>(relaxed = true)
  val sniper = AuctionSniper(ITEM_ID, auction, sniperListener)

  "reports lost if auction closes immediately" {
    sniper.auctionClosed()

    verify(exactly = 1) { sniperListener.sniperStateChanged(SniperSnapshot(ITEM_ID, 0, 0, LOST)) }
  }

  "reports lost if auction closes when bidding" {
    sniper.currentPrice(123, 45, FromOtherBidder)
    sniper.auctionClosed()

    verify(exactly = 1) { sniperListener.sniperStateChanged(SniperSnapshot(ITEM_ID, 123, 168, BIDDING)) }
    verify(exactly = 1) { sniperListener.sniperStateChanged(SniperSnapshot(ITEM_ID, 123, 168, LOST)) }
  }

  "reports won if auction closes when winning" {
    sniper.currentPrice(123, 45, FromSniper)
    sniper.auctionClosed()

    verify(exactly = 1) { sniperListener.sniperStateChanged(SniperSnapshot(ITEM_ID, 123, 0, WINNING)) }
    verify(exactly = 1) { sniperListener.sniperStateChanged(SniperSnapshot(ITEM_ID, 123, 0, WON)) }
  }

  "bids higher and reports bidding when new price arrives" {

    val price = 1001
    val increment = 25
    val bid = price + increment

    sniper.currentPrice(price, increment, FromOtherBidder)

    verify(exactly = 1) { auction.bid(bid) }
    verify(exactly = 1) { sniperListener.sniperStateChanged(SniperSnapshot(ITEM_ID, price, bid, BIDDING)) }
  }

  "reports is winning when current price comes from sniper" {

    val price = 1001
    val increment = 25

    sniper.currentPrice(price, increment, FromSniper)

    verify { auction wasNot Called }
    verify(exactly = 1) { sniperListener.sniperStateChanged(SniperSnapshot(ITEM_ID, price, 0, WINNING)) }
  }
}) {
  override fun isolationMode() = IsolationMode.InstancePerTest

  companion object {
    const val ITEM_ID = "1234"
  }
}
