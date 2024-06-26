package goos.ui.swing

import goos.ui.api.PortfolioListener
import goos.ui.api.SniperListener
import goos.ui.api.SniperNotifier
import goos.ui.api.SniperSnapshot
import goos.ui.api.stateText
import javax.swing.table.AbstractTableModel

internal class SnipersTableModel :
  AbstractTableModel(),
  SniperListener,
  PortfolioListener {

  private val sniperSnapshots = mutableListOf<SniperSnapshot>()

  override fun getColumnCount(): Int = Column.values().size
  override fun getRowCount(): Int = sniperSnapshots.size
  override fun getColumnName(column: Int): String = Column.at(
    column,
  ).title

  override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
    val sniperSnapshot = sniperSnapshots[rowIndex]
    return Column.at(columnIndex).valueIn(sniperSnapshot)
  }

  override fun sniperStateChanged(
    sniperSnapshot: SniperSnapshot,
  ) {
    val index = sniperSnapshots.indexOfFirstOrNull { it.isForSameItemAs(sniperSnapshot) }
    if (index == null) {
      addSniperSnapshot(sniperSnapshot)
    } else {
      sniperSnapshots[index] = sniperSnapshot
      fireTableRowsUpdated(index, index)
    }
  }

  private fun addSniperSnapshot(sniper: SniperSnapshot) {
    sniperSnapshots.add(sniper)
    fireTableRowsInserted(rowCount - 1, rowCount - 1)
  }

  override fun reset() {
    val rows = rowCount
    sniperSnapshots.clear()
    fireTableRowsDeleted(0, rows)
  }

  override fun sniperAdded(sniper: SniperNotifier) {
    sniper.addSniperListener(SwingThreadSniperListener(this))
  }
}

internal enum class Column(val title: String) {
  ITEM_IDENTIFIER("Item") {
    override fun valueIn(snapshot: SniperSnapshot) = snapshot.item.identifier
  },
  LAST_PRICE("Last Price") {
    override fun valueIn(snapshot: SniperSnapshot) = snapshot.lastPrice
  },
  LAST_BID("Last Bid") {
    override fun valueIn(snapshot: SniperSnapshot) = snapshot.lastBid
  },
  SNIPER_STATE("State") {
    override fun valueIn(snapshot: SniperSnapshot) = snapshot.stateText()
  }, ;

  abstract fun valueIn(snapshot: SniperSnapshot): Any

  companion object {
    fun at(offset: Int) = values()[offset]
  }
}

private fun <T> List<T>.indexOfFirstOrNull(predicate: (T) -> Boolean): Int? {
  val index = indexOfFirst(predicate)
  return if (index >= 0) index else null
}
