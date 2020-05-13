package goos.ui.swing

import goos.common.Defect
import goos.core.AuctionSniper
import goos.core.SniperCollector
import goos.ui.api.SniperListener
import goos.ui.api.UiSniperSnapshot
import goos.ui.api.UiSniperState.BIDDING
import goos.ui.api.UiSniperState.JOINING
import goos.ui.api.UiSniperState.LOST
import goos.ui.api.UiSniperState.WINNING
import goos.ui.api.UiSniperState.WON
import javax.swing.table.AbstractTableModel

class SnipersTableModel : AbstractTableModel(), SniperListener, SniperCollector {

  private val sniperSnapshots = mutableListOf<UiSniperSnapshot>()
  private val notToBeGCd = mutableListOf<AuctionSniper>()

  override fun getColumnCount(): Int = Column.values().size
  override fun getRowCount(): Int = sniperSnapshots.size
  override fun getColumnName(column: Int): String = Column.at(
    column
  ).title

  override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
    val sniperSnapshot = sniperSnapshots[rowIndex]
    return Column.at(columnIndex).valueIn(sniperSnapshot)
  }

  override fun sniperStateChanged(
    sniperSnapshot: UiSniperSnapshot
  ) {
    val index = sniperSnapshots.indexOfFirstOrNull { it.isForSameItemAs(sniperSnapshot) }
      ?: throw Defect("No sniper for same item as $sniperSnapshot")
    sniperSnapshots[index] = sniperSnapshot
    fireTableRowsUpdated(index, index)
  }

  fun addSniperSnapshot(sniper: UiSniperSnapshot) {
    sniperSnapshots.add(sniper)
    fireTableRowsInserted(rowCount - 1, rowCount - 1)
  }

  override fun reset() {
    val rows = rowCount
    sniperSnapshots.clear()
    fireTableRowsDeleted(0, rows)
  }

  override fun addSniper(sniper: AuctionSniper) {
    notToBeGCd.add(sniper)
    addSniperSnapshot(sniper.snapshot.toUi())
    sniper.addSniperListener(SwingThreadSniperListener(this))
  }
}

enum class Column(val title: String) {
  ITEM_IDENTIFIER("Item") {
    override fun valueIn(snapshot: UiSniperSnapshot) = snapshot.itemId
  },
  LAST_PRICE("Last Price") {
    override fun valueIn(snapshot: UiSniperSnapshot) = snapshot.lastPrice
  },
  LAST_BID("Last Bid") {
    override fun valueIn(snapshot: UiSniperSnapshot) = snapshot.lastBid
  },
  SNIPER_STATE("State") {
    override fun valueIn(snapshot: UiSniperSnapshot) = snapshot.stateText()
  };

  abstract fun valueIn(snapshot: UiSniperSnapshot): Any

  companion object {
    fun at(offset: Int) = values()[offset]
  }
}

internal fun UiSniperSnapshot.stateText(): String = when (state) {
  JOINING -> "Joining"
  BIDDING -> "Bidding"
  WINNING -> "Winning"
  LOST -> "Lost"
  WON -> "Won"
}

private fun <T> List<T>.indexOfFirstOrNull(predicate: (T) -> Boolean): Int? {
  val index = indexOfFirst(predicate)
  return if (index >= 0) index else null
}
