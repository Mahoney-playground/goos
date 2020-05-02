package goos.core

import goos.core.Column.ITEM_IDENTIFIER
import goos.core.Column.LAST_BID
import goos.core.Column.LAST_PRICE
import goos.core.Column.SNIPER_STATE
import goos.core.MainWindow.Companion.STATE_BIDDING
import goos.core.MainWindow.Companion.STATE_INITIAL
import goos.core.MainWindow.Companion.STATE_JOINING
import goos.core.MainWindow.Companion.STATE_LOST
import goos.core.MainWindow.Companion.STATE_WINNING
import goos.core.MainWindow.Companion.STATE_WON
import goos.core.SniperState.BIDDING
import goos.core.SniperState.JOINING
import goos.core.SniperState.LOST
import goos.core.SniperState.WINNING
import goos.core.SniperState.WON
import javax.swing.table.AbstractTableModel

class SnipersTableModel : AbstractTableModel() {

  private var _stateText: String = STATE_INITIAL

  internal var stateText: String
    get() = _stateText
    set(value) {
      _stateText = value
      fireTableRowsUpdated(0, 0)
    }

  private var sniperSnapshot = STARTING_UP

  override fun getColumnCount(): Int = Column.values().size
  override fun getRowCount(): Int = 1

  override fun getValueAt(rowIndex: Int, columnIndex: Int): Any = when (Column.at(columnIndex)) {
    ITEM_IDENTIFIER -> sniperSnapshot.itemId
    LAST_PRICE -> sniperSnapshot.lastPrice
    LAST_BID -> sniperSnapshot.lastBid
    SNIPER_STATE -> stateText
  }

  fun sniperStatusChanged(
    newSniperSnapshot: SniperSnapshot
  ) {
    val newStatusText = when (newSniperSnapshot.state) {
      JOINING -> STATE_JOINING
      BIDDING -> STATE_BIDDING
      WINNING -> STATE_WINNING
      LOST -> STATE_LOST
      WON -> STATE_WON
    }
    sniperSnapshot = newSniperSnapshot
    _stateText = newStatusText
    fireTableRowsUpdated(0, 0)
  }

  companion object {
    val STARTING_UP = SniperSnapshot("", 0, 0, JOINING)
  }
}

enum class Column {
  ITEM_IDENTIFIER,
  LAST_PRICE,
  LAST_BID,
  SNIPER_STATE;

  companion object {
    fun at(offset: Int) = values()[offset]
  }
}
