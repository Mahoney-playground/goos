package goos.core

import goos.core.Column.ITEM_IDENTIFIER
import goos.core.Column.LAST_BID
import goos.core.Column.LAST_PRICE
import goos.core.Column.SNIPER_STATUS
import goos.core.MainWindow.Companion.STATUS_INITIAL
import javax.swing.table.AbstractTableModel

class SnipersTableModel : AbstractTableModel() {

  private var _statusText: String = STATUS_INITIAL

  internal var statusText: String
    get() = _statusText
    set(value) {
      _statusText = value
      fireTableRowsUpdated(0, 0)
    }

  private var sniperSnapshot = STARTING_UP

  override fun getColumnCount(): Int = Column.values().size
  override fun getRowCount(): Int = 1

  override fun getValueAt(rowIndex: Int, columnIndex: Int): Any = when (Column.at(columnIndex)) {
    ITEM_IDENTIFIER -> sniperSnapshot.itemId
    LAST_PRICE -> sniperSnapshot.lastPrice
    LAST_BID -> sniperSnapshot.lastBid
    SNIPER_STATUS -> statusText
  }

  fun sniperStatusChanged(
    newSniperSnapshot: SniperSnapshot,
    newStatusText: String
  ) {
    sniperSnapshot = newSniperSnapshot
    _statusText = newStatusText
    fireTableRowsUpdated(0, 0)
  }

  companion object {
    val STARTING_UP = SniperSnapshot("", 0, 0)
  }
}

enum class Column {
  ITEM_IDENTIFIER,
  LAST_PRICE,
  LAST_BID,
  SNIPER_STATUS;

  companion object {
    fun at(offset: Int) = values()[offset]
  }
}