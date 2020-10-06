package goos.auction.xmpp

import goos.auction.api.auctionApiTests
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun xmppAuctionApiTests() = auctionApiTests(
  sniperId = "sniper@auctionhost.internal/Auction",
  auctionServer = XmppAuctionDriver("item-879"),
  auctionHouse = XMPPAuctionHouse(
    hostname = "auctionhost.internal",
    username = "sniper",
    password = "sniper"
  )
)
