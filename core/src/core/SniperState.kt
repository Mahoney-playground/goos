package goos.core

import goos.common.Defect

enum class SniperState(
  private val whenAuctionClosed: SniperState?
) {
  LOST(null),
  WON(null),
  JOINING(LOST),
  BIDDING(LOST),
  WINNING(WON);

  internal fun whenAuctionClosed(): SniperState =
    whenAuctionClosed ?: throw Defect("Auction is already closed")
}
