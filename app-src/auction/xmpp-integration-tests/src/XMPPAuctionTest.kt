package goos.auction.xmpp.integration

import goos.auction.xmpp.xmppAuctionApiTests
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.StringSpec
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class XMPPAuctionTest : StringSpec({
  include(xmppAuctionApiTests())
}) {
  override fun isolationMode() = InstancePerTest
}
