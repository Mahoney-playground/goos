package goos.core

import goos.common.Defect

enum class SniperState(
  private val whenAuctionClosed: SniperState?
) {
  LOST(null),
  WON(null),
  JOINING(LOST),
  BIDDING(LOST),
  WINNING(WON),
  LOSING(LOST),
  FAILED(null);

  internal fun whenAuctionClosed(): SniperState =
    whenAuctionClosed ?: throw Defect("Auction is already closed")

  internal fun toUiState() = when (this) {
    LOST -> goos.ui.SniperState.LOST
    WON -> goos.ui.SniperState.WON
    JOINING -> goos.ui.SniperState.JOINING
    BIDDING -> goos.ui.SniperState.BIDDING
    WINNING -> goos.ui.SniperState.WINNING
    LOSING -> goos.ui.SniperState.LOSING
    FAILED -> goos.ui.SniperState.FAILED
  }
}
