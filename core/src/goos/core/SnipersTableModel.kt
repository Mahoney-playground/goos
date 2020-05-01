package goos.core.goos.core

import goos.core.MainWindow.Companion.STATUS_INITIAL
import javax.swing.table.AbstractTableModel

class SnipersTableModel : AbstractTableModel() {

  internal var statusText: String = STATUS_INITIAL
    set(value) {
      field = value
      fireTableRowsUpdated(0, 0)
    }

  override fun getColumnCount(): Int = 1
  override fun getRowCount(): Int = 1

  override fun getValueAt(rowIndex: Int, columnIndex: Int) = statusText
}
