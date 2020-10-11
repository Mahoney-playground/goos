package goos

import goos.auction.stub.StubAuctionDriver
import goos.auction.stub.StubAuctionHouse
import goos.auction.stub.StubAuctionServer
import goos.core.Core
import goos.ui.stub.StubUi
import goos.ui.stub.StubUiDriver
import io.kotest.core.spec.style.StringSpec
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class AuctionSniperEndToEndStubbedTest : StringSpec({

  val stubAuctionServer = StubAuctionServer()
  val auctionHouse = StubAuctionHouse("", stubAuctionServer)

  val ui = StubUi()

  val app = Core(auctionHouse, ui)

  beforeSpec {
    Thread {
      app.run()
    }.start()
  }

  include(
    auctionSniperEndToEndTest(
      StubAuctionDriver("item-54321", stubAuctionServer),
      StubAuctionDriver("item-65432", stubAuctionServer),
      ApplicationRunner(StubUiDriver(ui))
    )
  )
})
