package goos.ui.api

import goos.ui.api.UiSniperState.JOINING

data class UiSniperSnapshot(
  val itemId: String,
  val lastPrice: Int,
  val lastBid: Int,
  val state: UiSniperState
) {

  fun isForSameItemAs(sniperSnapshot: UiSniperSnapshot) = itemId == sniperSnapshot.itemId

  companion object {
    fun joining(itemId: String) = UiSniperSnapshot(itemId, 0, 0, JOINING)
  }
}
