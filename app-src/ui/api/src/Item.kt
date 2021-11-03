package goos.ui.api

@JvmInline
value class ItemId(val value: String) {
  override fun toString() = value
}

fun CharSequence.toItemId() = ItemId(this.toString())

interface Item {
  val identifier: ItemId
  val stopPrice: Int
}
