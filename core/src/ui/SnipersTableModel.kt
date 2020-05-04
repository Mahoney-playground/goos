package goos.core.ui

import goos.core.core.Defect
import goos.core.core.SniperListener
import goos.core.core.SniperSnapshot
import goos.core.core.SniperState.BIDDING
import goos.core.core.SniperState.JOINING
import goos.core.core.SniperState.LOST
import goos.core.core.SniperState.WINNING
import goos.core.core.SniperState.WON
import javax.swing.table.AbstractTableModel

class SnipersTableModel : AbstractTableModel(), SniperListener {

  private val sniperSnapshots = mutableListOf<SniperSnapshot>()

  override fun getColumnCount(): Int = Column.values().size
  override fun getRowCount(): Int = sniperSnapshots.size
  override fun getColumnName(column: Int): String = Column.at(column).title

  override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
    val sniperSnapshot = sniperSnapshots[rowIndex]
    return Column.at(columnIndex).valueIn(sniperSnapshot)
  }

  override fun sniperStateChanged(
    sniperSnapshot: SniperSnapshot
  ) {
    val index = sniperSnapshots.indexOfFirst { it.itemId == sniperSnapshot.itemId }
    if (index >= 0) {
      sniperSnapshots[index] = sniperSnapshot
      fireTableRowsUpdated(index, index)
    } else {
      throw Defect("No sniper with id [${sniperSnapshot.itemId}]")
    }
  }

  fun addSniper(sniper: SniperSnapshot) {
    sniperSnapshots.add(sniper)
    fireTableRowsInserted(rowCount - 1, rowCount - 1)
  }

  fun reset() {
    val rows = rowCount
    sniperSnapshots.clear()
    fireTableRowsDeleted(0, rows)
  }
}

enum class Column(val title: String) {
  ITEM_IDENTIFIER("Item") { override fun valueIn(snapshot: SniperSnapshot) = snapshot.itemId },
  LAST_PRICE("Last Price") { override fun valueIn(snapshot: SniperSnapshot) = snapshot.lastPrice },
  LAST_BID("Last Bid") { override fun valueIn(snapshot: SniperSnapshot) = snapshot.lastBid },
  SNIPER_STATE("State") { override fun valueIn(snapshot: SniperSnapshot) = snapshot.stateText() };

  abstract fun valueIn(snapshot: SniperSnapshot): Any

  companion object {
    fun at(offset: Int) = values()[offset]
  }
}

internal fun SniperSnapshot.stateText(): String = when (state) {
  JOINING -> "Joining"
  BIDDING -> "Bidding"
  WINNING -> "Winning"
  LOST -> "Lost"
  WON -> "Won"
}
