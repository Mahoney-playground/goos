package goos.auction.api

@JvmInline
value class BidderId(private val value: String) {
  override fun toString() = value
}

fun CharSequence.toBidderId() = BidderId(this.toString())
