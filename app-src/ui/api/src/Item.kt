package goos.ui.api

data class Item(
  val identifier: String,
  private val stopPrice: Int
) {
  fun allowsBid(bid: Int): Boolean = bid <= stopPrice
}
