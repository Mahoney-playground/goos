package goos.auction.api

inline class BidderId(private val value: String) {
  override fun toString() = value
}

fun String.toBidderId() = BidderId(this)
