package goos.core

import goos.auction.api.Auction
import goos.auction.api.AuctionHouse
import goos.portfolio.Item
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

    val item = Item(itemId, stopPrice = 10_000)

    // when
    sniperLauncher.joinAuction(item)

    verifyOrder {
      auction.addAuctionEventListener(match { it is AuctionSniper && it.item == item })
      sniperPortfolio.addSniper(match { it.item == item })
      auction.join()
    }
  }
})
