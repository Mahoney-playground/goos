package goos.core

import goos.core.SniperState.BIDDING
import goos.core.SniperState.JOINING
import goos.core.SniperState.WINNING

data class SniperSnapshot(
  val itemId: String,
  val lastPrice: Int,
  val lastBid: Int,
  val state: SniperState
) {

  internal fun bidding(newLastPrice: Int, newLastBid: Int) =
    copy(lastPrice = newLastPrice, lastBid = newLastBid, state = BIDDING)

  internal fun winning(newLastPrice: Int) =
    copy(lastPrice = newLastPrice, state = WINNING)

  internal fun closed() =
    copy(state = state.whenAuctionClosed())

  fun isForSameItemAs(sniperSnapshot: SniperSnapshot) = itemId == sniperSnapshot.itemId

  companion object {
    fun joining(itemId: String) = SniperSnapshot(itemId, 0, 0, JOINING)
  }
}
