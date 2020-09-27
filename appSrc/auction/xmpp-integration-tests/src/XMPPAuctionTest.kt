package goos

import goos.auction.api.Auction
import goos.auction.api.AuctionEventListener
import goos.auction.api.AuctionEventListener.PriceSource
import goos.auction.xmpp.XMPPAuctionHouse
import goos.xmpptestsupport.AuctionDriver
import goos.xmpptestsupport.XmppAuctionDriver
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import uk.org.lidalia.kotlinlangext.coroutines.sync.CountDownLatch
import kotlin.time.ExperimentalTime

@ExperimentalTime
class XMPPAuctionTest : StringSpec({

  val auctionServer: AuctionDriver = XmppAuctionDriver("item-879")

  val auctionListener = mockk<AuctionEventListener>(relaxed = true)

  val auctionHouse = XMPPAuctionHouse(
    hostname = "auctionhost.internal",
    username = "sniper",
    password = "sniper"
  )

  val auction = auctionHouse
    .auctionFor(auctionServer.itemId)
    .apply {
      addAuctionEventListener(auctionListener)
    }

  "receives events from auction server after joining" {

    auctionServer.startSellingItem()
    auction.join()
    auctionServer.hasReceivedJoinRequestFrom("sniper@auctionhost.internal/Auction")
    auction.synchronously {
      auctionServer.announceClosed()
    }

    verify(exactly = 1) {
      auctionListener.auctionClosed()
    }
  }

  "stops receiving events from auction server after failure" {

    auctionServer.startSellingItem()
    auction.join()
    auctionServer.hasReceivedJoinRequestFrom("sniper@auctionhost.internal/Auction")

    auction.synchronously {
      auctionServer.sendInvalidMessageContaining("broken")
    }

    verify(exactly = 1) {
      auctionListener.auctionFailed()
    }

    auction.synchronously {
      auctionServer.reportPrice(price = 100, increment = 10, bidder = "other")
    }

    confirmNoFurtherInteractionsWith(auctionListener)
  }

  afterTest { auctionHouse.disconnect() }
}) {
  override fun isolationMode() = IsolationMode.InstancePerTest
}

private suspend fun Auction.synchronously(
  numberOfEvents: Int = 1,
  work: () -> Unit
) {
  val eventReceived = CountDownLatch(numberOfEvents)
  addAuctionEventListener(object : AuctionEventListener {
    override fun currentPrice(price: Int, increment: Int, source: PriceSource) =
      eventReceived.countDown()

    override fun auctionClosed() = eventReceived.countDown()
    override fun auctionFailed() = eventReceived.countDown()
  })
  work()
  eventReceived.await()
}

fun confirmNoFurtherInteractionsWith(vararg mocks: Any) = confirmVerified(*mocks)
