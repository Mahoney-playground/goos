package goos.portfolio

data class SniperSnapshot(
  val item: Item,
  val lastPrice: Int,
  val lastBid: Int,
  val state: SniperState
) {
  fun isForSameItemAs(sniperSnapshot: SniperSnapshot) = item == sniperSnapshot.item
}
