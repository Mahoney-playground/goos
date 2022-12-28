package goos.auction.stub

import goos.auction.api.auctionApiTests
import goos.auction.api.toAuctionId
import goos.auction.api.toBidderId
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.StringSpec
import kotlin.time.ExperimentalTime

@ExperimentalTime
class StubAuctionTest : StringSpec({

  val sniperId = "MY_SNIPER".toBidderId()
  val stubAuctionServer = StubAuctionServer()

  include(
    auctionApiTests(
      sniperId = sniperId,
      auctionServer = StubAuctionDriver(
        auctionId = "item-879".toAuctionId(),
        auctionServer = stubAuctionServer,
      ),
      auctionHouse = StubAuctionHouse(
        sniperId,
        stubAuctionServer,
      ),
    ),
  )
},) {
  override fun isolationMode() = InstancePerTest
}
