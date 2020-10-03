package goos

import goos.auction.api.Auction
import goos.auction.api.AuctionEventListener
import goos.auction.api.AuctionEventListener.PriceSource
import goos.auction.api.AuctionHouse
import goos.auction.stub.StubAuctionHouse
import goos.xmpptestsupport.AuctionDriver
import goos.xmpptestsupport.StubAuctionDriver
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.mockk
import io.mockk.verify
import uk.org.lidalia.kotlinlangext.coroutines.sync.CountDownLatch
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
class StubAuctionTest : StringSpec({

  val sniperId = "MY_SNIPER"
  val stubAuctionDriver = StubAuctionDriver("item-879")
  val auctionServer: AuctionDriver = stubAuctionDriver
  val stubAuctionHouse = StubAuctionHouse(
    sniperId,
    { id, message ->
      stubAuctionDriver.sendMessage(id, message)
    },
    { messageListener -> stubAuctionDriver.register(messageListener) }
  )
  val auctionHouse: AuctionHouse = stubAuctionHouse

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
}) {
  override fun isolationMode() = IsolationMode.InstancePerTest
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
