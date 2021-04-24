package goos.core

import goos.auction.api.Auction
import goos.auction.api.AuctionHouse
import goos.auction.api.toAuctionId
import goos.ui.api.Item
import goos.ui.api.ItemId
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verifyOrder
import uk.org.lidalia.kotlinlangext.concurrent.Gate

class SniperLauncherTest : StringSpec({

  val itemId = ItemId("item 123")
  val auction = mockk<Auction>(relaxed = true)
  val auctionHouse = mockk<AuctionHouse>(relaxed = true) {
    every { auctionFor(itemId.value.toAuctionId()) } returns auction
  }
  val sniperPortfolio = SniperPortfolio().apply { mockkObject(this) }
  val sniperLauncher = SniperLauncher(auctionHouse, sniperPortfolio, Gate.closed())

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
