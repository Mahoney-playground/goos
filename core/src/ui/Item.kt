package goos.ui

data class Item(
  val identifier: String,
  private val stopPrice: Int
) {
  internal fun allowsBid(bid: Int): Boolean = bid <= stopPrice
}
