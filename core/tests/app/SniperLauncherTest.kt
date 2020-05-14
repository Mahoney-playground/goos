package goos.app

import goos.auction.api.Auction
import goos.auction.api.AuctionHouse
import goos.core.AuctionSniper
import goos.core.SniperLauncher
import goos.core.SniperPortfolio
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verifyOrder

class SniperLauncherTest : StringSpec({

  val itemId = "item 123"
  val auction = mockk<Auction>(relaxed = true)
  val auctionHouse = mockk<AuctionHouse>(relaxed = true) {
    every { auctionFor(itemId) } returns auction
  }
  val sniperPortfolio = SniperPortfolio().apply { mockkObject(this) }
  val sniperLauncher = SniperLauncher(auctionHouse, sniperPortfolio)

  "adds new sniper to collector and then joins auction" {

    val stopPrice = 10_000

    // when
    sniperLauncher.joinAuction(itemId, stopPrice)

    verifyOrder {
      auction.addAuctionEventListener(match { it is AuctionSniper && it.itemId == itemId })
      sniperPortfolio.addSniper(match { it.itemId == itemId && it.stopPrice == stopPrice })
      auction.join()
    }
  }
})
