package goos.ui.api

import kotlin.time.ExperimentalTime

interface UiDriver : AutoCloseable {
  fun hasBasicAttributes()

  @ExperimentalTime
  fun showSniperState(
    itemId: String,
    lastPrice: Int,
    lastBid: Int,
    stateText: String
  )

  fun hasTitle(title: String)
  fun hasColumnTitles()
  fun reset()
  fun startBiddingFor(itemId: String, stopPrice: Int)
}
