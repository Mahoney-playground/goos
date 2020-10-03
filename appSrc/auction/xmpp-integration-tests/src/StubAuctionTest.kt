package goos

import goos.auction.stub.StubAuctionHouse
import goos.auction.stub.StubAuctionServer
import goos.xmpptestsupport.StubAuctionDriver
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.StringSpec
import kotlin.time.ExperimentalTime

@ExperimentalTime
class StubAuctionTest : StringSpec({

  val sniperId = "MY_SNIPER"
  val stubAuctionServer = StubAuctionServer()

  include(
    auctionApiTests(
      sniperId = sniperId,
      auctionServer = StubAuctionDriver(
        itemId = "item-879",
        auctionServer = stubAuctionServer
      ),
      auctionHouse = StubAuctionHouse(
        sniperId,
        stubAuctionServer
      ),
    )
  )
}) {
  override fun isolationMode() = InstancePerTest
}
