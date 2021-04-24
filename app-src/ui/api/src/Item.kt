package goos.ui.api

inline class ItemId(val value: String) {
  override fun toString() = value
}

fun CharSequence.toItemId() = ItemId(this.toString())

data class Item(
  val identifier: ItemId,
  private val stopPrice: Int
) {
  fun allowsBid(bid: Int): Boolean = bid <= stopPrice
}
