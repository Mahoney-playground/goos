package goos.ui.api

import kotlin.time.ExperimentalTime

interface UiDriver : AutoCloseable {
  fun hasBasicAttributes()

  @ExperimentalTime
  suspend fun showSniperState(
    itemId: ItemId,
    lastPrice: Int,
    lastBid: Int,
    stateText: String
  )

  fun hasTitle(title: String)
  fun hasColumnTitles()
  fun reset()
  fun startBiddingFor(itemId: ItemId, stopPrice: Int)
}
