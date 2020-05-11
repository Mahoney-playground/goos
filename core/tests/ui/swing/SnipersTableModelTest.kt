package goos.ui.swing

import goos.common.Defect
import goos.ui.api.UiSniperSnapshot
import goos.ui.api.UiSniperState.BIDDING
import goos.ui.swing.Column.ITEM_IDENTIFIER
import goos.ui.swing.Column.LAST_BID
import goos.ui.swing.Column.LAST_PRICE
import goos.ui.swing.Column.SNIPER_STATE
import io.kotest.assertions.throwables.shouldThrow
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

  infix fun List<Any>.shouldMatch(sniperSnapshot: UiSniperSnapshot) {
    get(ITEM_IDENTIFIER.ordinal) shouldBe sniperSnapshot.itemId
    get(LAST_BID.ordinal) shouldBe sniperSnapshot.lastBid
    get(LAST_PRICE.ordinal) shouldBe sniperSnapshot.lastPrice
    get(SNIPER_STATE.ordinal) shouldBe sniperSnapshot.stateText()
  }

  fun TableModelEvent.isAnEventAtRow(
    row: Int,
    eventType: Int
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

    val joining = UiSniperSnapshot.joining("item id")
    model.addSniper(joining)

    val bidding = joining.copy(state = BIDDING, lastPrice = 555, lastBid = 666)
    model.sniperStateChanged(bidding)

    model.row(0) shouldMatch bidding
    listener.events.forOne { it isAnUpdateToRow 0 }
  }

  "notifies listeners when adding a sniper" {
    val joining = UiSniperSnapshot.joining("item123")

    model.rowCount shouldBe 0

    model.addSniper(joining)

    model.rowCount shouldBe 1
    model.row(0) shouldMatch joining
    listener.events.forOne { it isAnInsertionAtRow 0 }
  }

  "holds snipers in addition order" {
    model.addSniper(UiSniperSnapshot.joining("item 0"))
    model.addSniper(UiSniperSnapshot.joining("item 1"))

    model.row(0).column(ITEM_IDENTIFIER) shouldBe "item 0"
    model.row(1).column(ITEM_IDENTIFIER) shouldBe "item 1"
  }

  "updates correct row for sniper" {

    val item0 = UiSniperSnapshot.joining("item 0")
    model.addSniper(item0)

    val item1 = UiSniperSnapshot.joining("item 1")
    model.addSniper(item1)

    val item2 = UiSniperSnapshot.joining("item 2")
    model.addSniper(item2)

    val updatedItem1 = item1.copy(state = BIDDING, lastPrice = 10, lastBid = 11)
    model.sniperStateChanged(updatedItem1)

    model.row(0) shouldMatch item0
    model.row(1) shouldMatch updatedItem1
    model.row(2) shouldMatch item2
  }

  "throws defect if no existing sniper for an update" {

    val sniperSnapshot = UiSniperSnapshot.joining("no such item")
    val e = shouldThrow<Defect> {
      model.sniperStateChanged(sniperSnapshot)
    }
    e.message shouldBe "No sniper for same item as $sniperSnapshot"
  }
}) {
  override fun isolationMode() = InstancePerTest
}

private fun <E> List<E>.column(enum: Enum<*>): E = get(enum.ordinal)

private fun SnipersTableModel.row(i: Int): List<Any> =
  (0 until columnCount).map { columnIndex ->
    getValueAt(i, columnIndex)
  }
