package goos.app

import goos.auction.api.Auction
import goos.auction.api.AuctionEventListener
import goos.auction.api.AuctionHouse
import goos.core.AuctionSniper
import goos.core.SniperCollector
import goos.core.SniperLauncher
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify

class SniperLauncherTest : StringSpec({

  val itemId = "item 123"
  val auction = StubAuction().apply { mockkObject(this) }
  val auctionHouse = mockk<AuctionHouse> {
    every { auctionFor(itemId) } returns auction
  }
  val sniperCollector = StubSniperCollector(auction).apply { mockkObject(this) }
  val sniperLauncher = SniperLauncher(auctionHouse, sniperCollector)

  "adds new sniper to collector and then joins auction" {

    // when
    sniperLauncher.joinAuction(itemId)

    verify(exactly = 1) {
      auction.addAuctionEventListener(match { it is AuctionSniper && it.itemId == itemId })
      sniperCollector.addSniper(match { it.itemId == itemId })
      auction.join()
    }
  }
})

class StubAuction : Auction {

  enum class State { NotJoined, Joined }

  var state = State.NotJoined
    private set

  override fun addAuctionEventListener(listener: AuctionEventListener) {
    state shouldBe State.NotJoined
  }

  override fun join() { state = State.Joined }

  override fun bid(bid: Int) { state shouldBe State.Joined }
}

class StubSniperCollector(
  private val auction: StubAuction
) : SniperCollector {

  override fun addSniper(sniper: AuctionSniper) {
    auction.state shouldBe StubAuction.State.NotJoined
  }

  override fun reset() {}
}
