package goos

import goos.auction.api.AuctionEventListener
import goos.auction.api.AuctionEventListener.PriceSource
import goos.auction.xmpp.XMPPAuctionHouse
import goos.xmpptestsupport.FakeAuctionServer
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.SECONDS

class XMPPAuctionTest : StringSpec({

  "receives events from auction server after joining" {
    val auctionWasClosed = CountDownLatch(1)

    val auctionServer = FakeAuctionServer("item-879")
    auctionServer.startSellingItem()

    val auction = XMPPAuctionHouse(
        hostname = "auctionhost.internal",
        username = "sniper",
        password = "sniper"
    )
      .auctionFor(auctionServer.itemId)
      .apply {
        addAuctionEventListener(auctionClosedListener(auctionWasClosed))
      }

    auction.join()
    auctionServer.hasReceivedJoinRequestFrom("sniper@auctionhost.internal/Auction")
    auctionServer.announceClosed()

    withContext(Dispatchers.IO) {
      auctionWasClosed.await(2, SECONDS) shouldBe true
    }
  }
})

fun auctionClosedListener(
  auctionWasClosed: CountDownLatch
) = object : AuctionEventListener {
  override fun auctionClosed() { auctionWasClosed.countDown() }
  override fun currentPrice(price: Int, increment: Int, source: PriceSource) {}
}
