package goos.core

import goos.core.SniperState.BIDDING
import goos.core.SniperState.JOINING
import goos.core.SniperState.LOST
import goos.core.SniperState.WINNING
import goos.core.SniperState.WON
import javax.swing.table.AbstractTableModel

class SnipersTableModel : AbstractTableModel(), SniperListener {

  private var sniperSnapshot = STARTING_UP

  override fun getColumnCount(): Int = Column.values().size
  override fun getRowCount(): Int = 1

  override fun getValueAt(rowIndex: Int, columnIndex: Int): Any =
    Column.at(columnIndex).valueIn(sniperSnapshot)

  override fun sniperStateChanged(
    sniperSnapshot: SniperSnapshot
  ) {
    this.sniperSnapshot = sniperSnapshot
    fireTableRowsUpdated(0, 0)
  }

  companion object {

    private val STARTING_UP = SniperSnapshot("", 0, 0, JOINING)
  }
}

enum class Column {
  ITEM_IDENTIFIER { override fun valueIn(snapshot: SniperSnapshot) = snapshot.itemId },
  LAST_PRICE { override fun valueIn(snapshot: SniperSnapshot) = snapshot.lastPrice },
  LAST_BID { override fun valueIn(snapshot: SniperSnapshot) = snapshot.lastBid },
  SNIPER_STATE { override fun valueIn(snapshot: SniperSnapshot) = snapshot.stateText() };

  abstract fun valueIn(snapshot: SniperSnapshot): Any

  companion object {
    fun at(offset: Int) = values()[offset]

    private fun SniperSnapshot.stateText(): String = when (state) {
      JOINING -> "Joining"
      BIDDING -> "Bidding"
      WINNING -> "Winning"
      LOST -> "Lost"
      WON -> "Won"
    }
  }
}
