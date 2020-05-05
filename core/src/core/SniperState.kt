package goos.core.core

import goos.core.common.Defect
import goos.core.ui.api.UiSniperState

enum class SniperState(
  private val whenAuctionClosed: SniperState?,
  val ui: UiSniperState
) {
  LOST(null, UiSniperState.LOST),
  WON(null, UiSniperState.WON),
  JOINING(LOST, UiSniperState.JOINING),
  BIDDING(LOST, UiSniperState.BIDDING),
  WINNING(WON, UiSniperState.WINNING);

  internal fun whenAuctionClosed(): SniperState =
    whenAuctionClosed ?: throw Defect("Auction is already closed")
}
