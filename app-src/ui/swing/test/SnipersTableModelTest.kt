package goos.ui.swing

import goos.ui.api.Item
import goos.ui.api.SniperSnapshot
import goos.ui.api.SniperState
import goos.ui.api.SniperState.BIDDING
import goos.ui.api.SniperState.JOINING
import goos.ui.api.stateText
import goos.ui.api.toItemId
import goos.ui.common.ItemData
import goos.ui.swing.Column.ITEM_IDENTIFIER
import goos.ui.swing.Column.LAST_BID
import goos.ui.swing.Column.LAST_PRICE
import goos.ui.swing.Column.SNIPER_STATE
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forOne
import io.kotest.matchers.shouldBe
import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelEvent.ALL_COLUMNS
import javax.swing.event.TableModelEvent.INSERT
import javax.swing.event.TableModelEvent.UPDATE
import javax.swing.event.TableModelListener

class SnipersTableModelTest : StringSpec({

  val listener = object : TableModelListener {

    val events = mutableListOf<TableModelEvent>()

    override fun tableChanged(e: TableModelEvent) {
      events.add(e)
    }
  }

  val model = SnipersTableModel().apply {
    addTableModelListener(listener)
  }

  infix fun List<Any>.shouldMatch(sniperSnapshot: SniperSnapshot) {
    get(ITEM_IDENTIFIER.ordinal) shouldBe sniperSnapshot.item.identifier
    get(LAST_BID.ordinal) shouldBe sniperSnapshot.lastBid
    get(LAST_PRICE.ordinal) shouldBe sniperSnapshot.lastPrice
    get(SNIPER_STATE.ordinal) shouldBe sniperSnapshot.stateText()
  }

  fun TableModelEvent.isAnEventAtRow(
    row: Int,
    eventType: Int,
  ) {
    source shouldBe model
    firstRow shouldBe row
    lastRow shouldBe row
    column shouldBe ALL_COLUMNS
    type shouldBe eventType
  }

  infix fun TableModelEvent.isAnInsertionAtRow(row: Int) = isAnEventAtRow(row, INSERT)

  infix fun TableModelEvent.isAnUpdateToRow(row: Int) = isAnEventAtRow(row, UPDATE)

  "has enough columns" {
    model.columnCount shouldBe Column.values().size
  }

  Column.values().forEach { column ->
    "has column title for $column" {
      column.title shouldBe model.getColumnName(column.ordinal)
    }
  }

  "sets sniper values in columns" {

    val joining = joining(ItemData("item id".toItemId(), 1_000))
    model.sniperStateChanged(joining)

    val bidding = joining.copy(state = BIDDING, lastPrice = 555, lastBid = 666)
    model.sniperStateChanged(bidding)

    model.row(0) shouldMatch bidding
    listener.events.forOne { it isAnUpdateToRow 0 }
  }

  "notifies listeners when adding a sniper" {
    val joining = joining(ItemData("item123".toItemId(), 1_000))

    model.rowCount shouldBe 0

    model.sniperStateChanged(joining)

    model.rowCount shouldBe 1
    model.row(0) shouldMatch joining
    listener.events.forOne { it isAnInsertionAtRow 0 }
  }

  "holds snipers in addition order" {
    model.sniperStateChanged(
      joining(
        ItemData(
          "item 0".toItemId(),
          1_000,
        ),
      ),
    )
    model.sniperStateChanged(
      joining(
        ItemData(
          "item 1".toItemId(),
          1_000,
        ),
      ),
    )

    model.row(0).column(ITEM_IDENTIFIER) shouldBe "item 0".toItemId()
    model.row(1).column(ITEM_IDENTIFIER) shouldBe "item 1".toItemId()
  }

  "updates correct row for sniper" {

    val item0 = joining(ItemData("item 0".toItemId(), 1_000))
    model.sniperStateChanged(item0)

    val item1 = joining(ItemData("item 1".toItemId(), 1_000))
    model.sniperStateChanged(item1)

    val item2 = joining(ItemData("item 2".toItemId(), 1_000))
    model.sniperStateChanged(item2)

    val updatedItem1 = item1.copy(state = BIDDING, lastPrice = 10, lastBid = 11)
    model.sniperStateChanged(updatedItem1)

    model.row(0) shouldMatch item0
    model.row(1) shouldMatch updatedItem1
    model.row(2) shouldMatch item2
  }
},) {
  override fun isolationMode() = InstancePerTest
}

private fun joining(item: Item) =
  SniperSnapshotData(item, 0, 0, JOINING)

private fun <E> List<E>.column(enum: Enum<*>): E = get(enum.ordinal)

private fun SnipersTableModel.row(i: Int): List<Any> =
  (0 until columnCount).map { columnIndex ->
    getValueAt(i, columnIndex)
  }

data class SniperSnapshotData(
  override val item: Item,
  override val lastPrice: Int,
  override val lastBid: Int,
  override val state: SniperState,
) : SniperSnapshot
