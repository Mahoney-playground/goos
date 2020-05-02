package goos.core

import goos.core.Column.ITEM_IDENTIFIER
import goos.core.Column.LAST_BID
import goos.core.Column.LAST_PRICE
import goos.core.Column.SNIPER_STATE
import goos.core.SniperState.BIDDING
import goos.core.SniperState.JOINING
import goos.core.SniperState.LOST
import goos.core.SniperState.WINNING
import goos.core.SniperState.WON
import javax.swing.table.AbstractTableModel

class SnipersTableModel : AbstractTableModel() {

  private var sniperSnapshot = STARTING_UP

  override fun getColumnCount(): Int = Column.values().size
  override fun getRowCount(): Int = 1

  override fun getValueAt(rowIndex: Int, columnIndex: Int): Any = when (Column.at(columnIndex)) {
    ITEM_IDENTIFIER -> sniperSnapshot.itemId
    LAST_PRICE -> sniperSnapshot.lastPrice
    LAST_BID -> sniperSnapshot.lastBid
    SNIPER_STATE -> sniperSnapshot.stateText()
  }

  fun sniperStateChanged(
    newSniperSnapshot: SniperSnapshot
  ) {
    sniperSnapshot = newSniperSnapshot
    fireTableRowsUpdated(0, 0)
  }

  private fun SniperSnapshot.stateText(): String = when (state) {
    JOINING -> STATE_JOINING
    BIDDING -> STATE_BIDDING
    WINNING -> STATE_WINNING
    LOST -> STATE_LOST
    WON -> STATE_WON
  }

  companion object {

    private val STARTING_UP = SniperSnapshot("", 0, 0, JOINING)

    private const val STATE_LOST: String = "Lost"
    private const val STATE_BIDDING: String = "Bidding"
    private const val STATE_JOINING: String = "Joining"
    private const val STATE_WINNING: String = "Winning"
    private const val STATE_WON: String = "Won"
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
