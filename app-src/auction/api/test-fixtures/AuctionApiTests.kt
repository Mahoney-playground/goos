package goos.auction.api

import goos.auction.api.AuctionEventListener.PriceSource
import io.kotest.core.factory.TestFactory
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import uk.org.lidalia.kotlinlangext.coroutines.sync.CountDownLatch
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun auctionApiTests(
  sniperId: BidderId,
  auctionServer: AuctionDriver,
  auctionHouse: AuctionHouse,
): TestFactory = stringSpec {
  val auctionListener = mockk<AuctionEventListener>(relaxed = true)

  "receives events from auction server after joining" {
    auctionServer.startSellingItem()

    val auction = auctionHouse.getAuction(auctionServer.auctionId, auctionListener)
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

    val auction = auctionHouse.getAuction(auctionServer.auctionId, auctionListener)
    auction.join()
    auctionServer.hasReceivedJoinRequestFrom(sniperId)

    auction.synchronously {
      auctionServer.sendInvalidMessageContaining("broken")
    }

    verify(exactly = 1) {
      auctionListener.auctionFailed()
    }

    auction.synchronously {
      auctionServer.reportPrice(price = 100, increment = 10, bidder = "other".toBidderId())
    }

    confirmNoFurtherInteractionsWith(auctionListener)
  }

  afterTest { auctionHouse.disconnect() }
}

private fun AuctionHouse.getAuction(
  auctionId: AuctionId,
  auctionListener: AuctionEventListener,
): Auction = auctionFor(auctionId)
  .apply {
    addAuctionEventListener(auctionListener)
  }

@ExperimentalTime
private suspend fun Auction.synchronously(
  numberOfEvents: Int = 1,
  work: () -> Unit,
) {
  val eventReceived = CountDownLatch(numberOfEvents)
  addAuctionEventListener(
    object : AuctionEventListener {
      override fun currentPrice(price: Int, increment: Int, source: PriceSource) =
        eventReceived.countDown()

      override fun auctionClosed() = eventReceived.countDown()
      override fun auctionFailed() = eventReceived.countDown()
    },
  )
  work()
  eventReceived.await(5.seconds).shouldBeTrue()
}

private fun confirmNoFurtherInteractionsWith(vararg mocks: Any) = confirmVerified(*mocks)
