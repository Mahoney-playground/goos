package goos.core

import goos.core.AuctionEventListener.PriceSource.FromOtherBidder
import goos.core.AuctionEventListener.PriceSource.FromSniper
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

    verify { sniperListener.sniperLost() }
  }

  "reports lost if auction closes when bidding" {
    sniper.currentPrice(123, 45, FromOtherBidder)
    sniper.auctionClosed()

    verify { sniperListener.sniperLost() }
  }

  "reports won if auction closes when winning" {
    sniper.currentPrice(123, 45, FromSniper)
    sniper.auctionClosed()

    verify { sniperListener.sniperWon() }
  }

  "bids higher and reports bidding when new price arrives" {

    val price = 1001
    val increment = 25
    val bid = price + increment

    sniper.currentPrice(price, increment, FromOtherBidder)

    verify(exactly = 1) { auction.bid(bid) }
    verify { sniperListener.sniperBidding(SniperState(ITEM_ID, price, bid)) }
  }

  "reports is winning when current price comes from sniper" {

    val price = 1001
    val increment = 25

    sniper.currentPrice(price, increment, FromSniper)

    verify { auction wasNot Called }
    verify { sniperListener.sniperWinning() }
  }
}) {
  override fun isolationMode() = IsolationMode.InstancePerTest

  companion object {
    const val ITEM_ID = "1234"
  }
}
