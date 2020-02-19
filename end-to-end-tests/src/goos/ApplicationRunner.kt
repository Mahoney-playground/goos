package goos

import goos.FakeAuctionServer.Companion.XMPP_HOSTNAME
import goos.core.Main
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
class ApplicationRunner {

  private var driver: AuctionSniperDriver? = null

  fun startBiddingIn(
    auction: FakeAuctionServer
  ) {

    val thread = Thread({
      Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD)
    }, "Test Application")
    thread.isDaemon = true
    thread.start()

    driver = AuctionSniperDriver(timeout = 1.seconds)

    driver!!.showSniperStatus(Main.STATUS_JOINING)
  }

  fun showSniperHasLostAuction() {
    driver!!.showSniperStatus(Main.STATUS_LOST)
  }

  fun stop() {
    driver?.dispose()
  }

  companion object {
    const val SNIPER_ID = "sniper"
    const val SNIPER_PASSWORD = "sniper"
  }
}
