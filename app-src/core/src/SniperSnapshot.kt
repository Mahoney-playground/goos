package goos.core

import goos.core.SniperState.BIDDING
import goos.core.SniperState.FAILED
import goos.core.SniperState.JOINING
import goos.core.SniperState.LOSING
import goos.core.SniperState.WINNING
import goos.ui.api.Item

data class SniperSnapshot(
  override val item: Item,
  override val lastPrice: Int,
  override val lastBid: Int,
  private val coreState: SniperState,
) : goos.ui.api.SniperSnapshot {

  override val state: goos.ui.api.SniperState = coreState.toUiState()

  internal fun bidding(newLastPrice: Int, newLastBid: Int) =
    copy(lastPrice = newLastPrice, lastBid = newLastBid, coreState = BIDDING)

  internal fun winning(newLastPrice: Int) =
    copy(lastPrice = newLastPrice, coreState = WINNING)

  internal fun losing(newLastPrice: Int) =
    copy(lastPrice = newLastPrice, coreState = LOSING)

  internal fun closed() =
    copy(coreState = coreState.whenAuctionClosed())

  fun failed() =
    copy(lastPrice = 0, lastBid = 0, coreState = FAILED)

  companion object {
    fun joining(item: Item) = SniperSnapshot(item, 0, 0, JOINING)
  }
}

fun SniperState.toUiState() = when (this) {
  SniperState.LOST -> goos.ui.api.SniperState.LOST
  SniperState.WON -> goos.ui.api.SniperState.WON
  JOINING -> goos.ui.api.SniperState.JOINING
  BIDDING -> goos.ui.api.SniperState.BIDDING
  WINNING -> goos.ui.api.SniperState.WINNING
  LOSING -> goos.ui.api.SniperState.LOSING
  FAILED -> goos.ui.api.SniperState.FAILED
}
