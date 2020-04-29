package goos.core

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.mockk.mockk
import io.mockk.verify

class AuctionSniperTest : StringSpec({

  val auction = mockk<Auction>(relaxed = true)
  val sniperListener = mockk<SniperListener>(relaxed = true)
  val sniper = AuctionSniper(auction, sniperListener)

  "reports lost when auction closes" {
    sniper.auctionClosed()

    verify {
      sniperListener.sniperLost()
    }
  }

  "bids higher and reports bidding when new price arrives" {

    val price = 1001
    val increment = 25

    sniper.currentPrice(price, increment)

    verify(exactly = 1) {
      auction.bid(price + increment)
    }
    verify {
      sniperListener.sniperBidding()
    }
  }
}) {
  override fun isolationMode() = IsolationMode.InstancePerTest
}
