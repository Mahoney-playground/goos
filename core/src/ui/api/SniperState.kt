package goos.core.ui.api

import goos.core.common.Defect

enum class SniperState(
  private val whenAuctionClosed: SniperState?
) {
  LOST(null),
  WON(null),
  JOINING(LOST),
  BIDDING(LOST),
  WINNING(WON);

  fun whenAuctionClosed(): SniperState =
    whenAuctionClosed ?: throw Defect("Auction is already closed")
}
