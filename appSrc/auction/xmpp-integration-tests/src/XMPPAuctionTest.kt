package goos

import goos.auction.api.auctionApiTests
import goos.auction.xmpp.XMPPAuctionHouse
import goos.auction.xmpp.XmppAuctionDriver
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.StringSpec
import kotlin.time.ExperimentalTime

@ExperimentalTime
class XMPPAuctionTest : StringSpec({

  include(
    auctionApiTests(
      sniperId = "sniper@auctionhost.internal/Auction",
      auctionServer = XmppAuctionDriver("item-879"),
      auctionHouse = XMPPAuctionHouse(
        hostname = "auctionhost.internal",
        username = "sniper",
        password = "sniper"
      )
    )
  )
}) {
  override fun isolationMode() = InstancePerTest
}
