package goos

import goos.auction.api.Auction
import goos.auction.api.AuctionEventListener
import goos.auction.api.AuctionEventListener.PriceSource
import goos.auction.api.AuctionHouse
import goos.xmpptestsupport.AuctionDriver
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import uk.org.lidalia.kotlinlangext.coroutines.sync.CountDownLatch
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
fun auctionApiTests(
  sniperId: String,
  auctionServer: AuctionDriver,
  auctionHouse: AuctionHouse
) = stringSpec {
  val auctionListener = mockk<AuctionEventListener>(relaxed = true)

  "receives events from auction server after joining" {

    auctionServer.startSellingItem()

    val auction = auctionHouse.getAuction(auctionServer.itemId, auctionListener)
    auction.join()
    auctionServer.hasReceivedJoinRequestFrom(sniperId)
    auction.synchronously {
      auctionServer.announceClosed()
    }

    verify(exactly = 1) {
      auctionListener.auctionClosed()
    }
  }

  "stops receiving events from auction server after failure" {

    auctionServer.startSellingItem()

    val auction = auctionHouse.getAuction(auctionServer.itemId, auctionListener)
    auction.join()
    auctionServer.hasReceivedJoinRequestFrom(sniperId)

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
}

private fun AuctionHouse.getAuction(
  itemId: String,
  auctionListener: AuctionEventListener
): Auction = auctionFor(itemId)
  .apply {
    addAuctionEventListener(auctionListener)
  }

@ExperimentalTime
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
  eventReceived.await(5.seconds).shouldBeTrue()
}

fun confirmNoFurtherInteractionsWith(vararg mocks: Any) = confirmVerified(*mocks)
