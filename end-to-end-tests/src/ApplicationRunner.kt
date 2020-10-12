package goos

import goos.auction.api.AuctionDriver
import goos.ui.api.UiDriver
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class ApplicationRunner(
  private val driver: UiDriver
) {

  fun hasCorrectAppearance() {
    driver.hasBasicAttributes()
    driver.hasTitle("Auction Sniper")
    driver.hasColumnTitles()
  }

  suspend fun startBiddingIn(
    auction: AuctionDriver,
    stopPrice: Int = Int.MAX_VALUE
  ) {
    driver.startBiddingFor(auction.itemId, stopPrice)
    driver.showSniperState(auction.itemId, 0, 0, STATE_JOINING)
  }

  suspend fun startBiddingIn(vararg auctions: AuctionDriver) = auctions.forEach { startBiddingIn(it) }

  suspend fun showSniperHasLostAuction(auction: AuctionDriver, lastPrice: Int, lastBid: Int) {
    driver.showSniperState(auction.itemId, lastPrice, lastBid, STATE_LOST)
  }

  suspend fun hasShownSniperIsBidding(auction: AuctionDriver, lastPrice: Int, lastBid: Int) {
    driver.showSniperState(auction.itemId, lastPrice, lastBid, STATE_BIDDING)
  }

  suspend fun hasShownSniperIsWinning(auction: AuctionDriver, winningBid: Int) {
    driver.showSniperState(auction.itemId, winningBid, winningBid, STATE_WINNING)
  }

  suspend fun hasShownSniperIsLosing(auction: AuctionDriver, lastPrice: Int, lastBid: Int) {
    driver.showSniperState(auction.itemId, lastPrice, lastBid, STATE_LOSING)
  }

  suspend fun showSniperHasWonAuction(auction: AuctionDriver, lastPrice: Int) {
    driver.showSniperState(auction.itemId, lastPrice, lastPrice, STATE_WON)
  }

  suspend fun showsSniperHasFailed(auction: AuctionDriver) {
    driver.showSniperState(auction.itemId, 0, 0, STATE_FAILED)
  }

  fun reset() {
    driver.reset()
  }

  companion object {
    const val SNIPER_XMPP_ID: String = "sniper@auctionhost.internal/Auction"
    const val STATE_LOST: String = "Lost"
    const val STATE_LOSING: String = "Losing"
    const val STATE_BIDDING: String = "Bidding"
    const val STATE_JOINING: String = "Joining"
    const val STATE_WINNING: String = "Winning"
    const val STATE_WON: String = "Won"
    const val STATE_FAILED: String = "Failed"
  }
}
