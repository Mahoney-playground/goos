package goos.core

import goos.core.Column.ITEM_IDENTIFIER
import goos.core.MainWindow.Companion.STATUS_BIDDING
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

  "set sniper values in columns" {
    model.sniperStatusChanged(SniperSnapshot("item id", 555, 666), STATUS_BIDDING)

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
