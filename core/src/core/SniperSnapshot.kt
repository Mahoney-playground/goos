package goos.core.core

import goos.core.core.SniperState.BIDDING
import goos.core.core.SniperState.JOINING
import goos.core.core.SniperState.WINNING

data class SniperSnapshot(
  val itemId: String,
  val lastPrice: Int,
  val lastBid: Int,
  val state: SniperState
) {

  fun bidding(newLastPrice: Int, newLastBid: Int) =
    SniperSnapshot(itemId, newLastPrice, newLastBid, BIDDING)

  fun winning(newLastPrice: Int) =
    SniperSnapshot(itemId, newLastPrice, lastBid, WINNING)

  fun closed() =
    SniperSnapshot(itemId, lastPrice, lastBid, state.whenAuctionClosed())

  fun isForSameItemAs(sniperSnapshot: SniperSnapshot) = itemId == sniperSnapshot.itemId

  companion object {
    fun joining(itemId: String) = SniperSnapshot(itemId, 0, 0, JOINING)
  }
}
