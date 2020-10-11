package goos.ui.stub

import goos.ui.api.UiDriver
import kotlin.time.ExperimentalTime

class StubUiDriver(
  private val ui: StubUi
) : UiDriver {

  override fun hasBasicAttributes() {}

  @ExperimentalTime
  override fun showSniperState(itemId: String, lastPrice: Int, lastBid: Int, stateText: String) {
    TODO("not implemented")
  }

  override fun hasTitle(title: String) {
    TODO("not implemented")
  }

  override fun hasColumnTitles() {
    TODO("not implemented")
  }

  override fun reset() {
    ui.reset()
  }

  override fun startBiddingFor(itemId: String, stopPrice: Int) {
    TODO("not implemented")
  }

  override fun close() {
    TODO("not implemented")
  }

}
