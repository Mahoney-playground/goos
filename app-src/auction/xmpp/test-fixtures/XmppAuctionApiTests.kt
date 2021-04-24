package goos.auction.xmpp

import goos.auction.api.auctionApiTests
import goos.auction.api.toAuctionId
import goos.auction.api.toBidderId
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun xmppAuctionApiTests() = auctionApiTests(
  sniperId = "sniper@auctionhost.internal/Auction".toBidderId(),
  auctionServer = XmppAuctionDriver("item-879".toAuctionId()),
  auctionHouse = XMPPAuctionHouse(
    hostname = "auctionhost.internal",
    username = "sniper",
    password = "sniper"
  )
)
