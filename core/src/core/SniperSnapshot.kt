package goos.core

import goos.core.SniperState.BIDDING
import goos.core.SniperState.JOINING
import goos.core.SniperState.LOSING
import goos.core.SniperState.WINNING

data class SniperSnapshot(
  val item: Item,
  val lastPrice: Int,
  val lastBid: Int,
  val state: SniperState
) {

  internal fun bidding(newLastPrice: Int, newLastBid: Int) =
    copy(lastPrice = newLastPrice, lastBid = newLastBid, state = BIDDING)

  internal fun winning(newLastPrice: Int) =
    copy(lastPrice = newLastPrice, state = WINNING)

  internal fun losing(newLastPrice: Int) =
    copy(lastPrice = newLastPrice, state = LOSING)

  internal fun closed() =
    copy(state = state.whenAuctionClosed())

  fun isForSameItemAs(sniperSnapshot: SniperSnapshot) = item == sniperSnapshot.item

  companion object {
    fun joining(item: Item) = SniperSnapshot(item, 0, 0, JOINING)
  }
}
