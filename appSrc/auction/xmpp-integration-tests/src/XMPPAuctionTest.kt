package goos

import goos.auction.xmpp.XMPPAuctionHouse
import goos.xmpptestsupport.XmppAuctionDriver
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import kotlin.time.ExperimentalTime

@ExperimentalTime
class XMPPAuctionTest : StringSpec({

  val sniperId = "sniper@auctionhost.internal/Auction"

  include(auctionApiTests(
    sniperId = sniperId,
    auctionServer = XmppAuctionDriver("item-879"),
    auctionHouse = XMPPAuctionHouse(
      hostname = "auctionhost.internal",
      username = "sniper",
      password = "sniper"
    )
  ))
}) {
  override fun isolationMode() = IsolationMode.InstancePerTest
}
