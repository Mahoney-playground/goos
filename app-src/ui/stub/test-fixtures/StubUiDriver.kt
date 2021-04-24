package goos.ui.stub

import goos.ui.api.ItemId
import goos.ui.api.UiDriver
import io.kotest.matchers.shouldBe
import kotlin.time.ExperimentalTime

class StubUiDriver(
  private val ui: StubUi
) : UiDriver {

  override fun hasBasicAttributes() {}

  @ExperimentalTime
  override suspend fun showSniperState(
    itemId: ItemId,
    lastPrice: Int,
    lastBid: Int,
    stateText: String,
  ) {
    ui.snipers[itemId] shouldBe SniperRow(itemId, lastPrice, lastBid, stateText)
  }

  override fun hasTitle(title: String) {
    ui.title shouldBe title
  }

  override fun hasColumnTitles() {
    ui.columnTitles shouldBe listOf("Item", "Last Price", "Last Bid", "State")
  }

  override fun reset() {
    ui.clickResetButton()
  }

  override fun startBiddingFor(itemId: ItemId, stopPrice: Int) {
    ui.itemField = itemId.value
    ui.stopPriceField = stopPrice.toString()
    ui.clickBidButton()
  }

  override fun close() {
    TODO("not implemented")
  }
}
