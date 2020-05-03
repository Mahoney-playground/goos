package goos.core.ui

import goos.core.core.SniperSnapshot
import goos.core.core.SniperState.BIDDING
import goos.core.ui.Column.ITEM_IDENTIFIER
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forOne
import io.kotest.matchers.shouldBe
import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelEvent.ALL_COLUMNS
import javax.swing.event.TableModelEvent.UPDATE
import javax.swing.event.TableModelListener

class SnipersTableModelTest : StringSpec({

  val listener = object : TableModelListener {

    val calls = mutableListOf<TableModelEvent>()

    override fun tableChanged(e: TableModelEvent) {
      calls.add(e)
    }
  }

  val model = SnipersTableModel().apply {
    addTableModelListener(listener)
  }

  infix fun Column.shouldHaveValue(expected: String) {
    model.getValueAt(rowIndex = 0, columnIndex = ordinal) shouldBe expected
  }

  "has enough columns" {
    model.columnCount shouldBe Column.values().size
  }

  Column.values().forEach { column ->
    "has column title for $column" {
      column.title shouldBe model.getColumnName(column.ordinal)
    }
  }

  "set sniper values in columns" {
    model.sniperStateChanged(SniperSnapshot("item id", 555, 666, BIDDING))

    ITEM_IDENTIFIER shouldHaveValue "item id"
    listener.calls.forOne {
      it.source shouldBe model
      it.firstRow shouldBe 0
      it.lastRow shouldBe 0
      it.column shouldBe ALL_COLUMNS
      it.type shouldBe UPDATE
    }
  }
}) {
  override fun isolationMode() = InstancePerTest
}
