package goos.core.ui

import goos.core.app.Main
import goos.core.core.SniperSnapshot
import goos.core.core.SniperState.JOINING
import java.awt.BorderLayout
import java.awt.BorderLayout.NORTH
import java.awt.BorderLayout.SOUTH
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.SwingUtilities.invokeLater

class MainWindow(
  private val main: Main,
  private val snipers: SnipersTableModel
) : JFrame("Auction Sniper") {

  init {
    name = MAIN_WINDOW_NAME
    fillContentPane(makeSnipersTable())
    pack()
    defaultCloseOperation = EXIT_ON_CLOSE
    isVisible = true
  }

  private fun makeSnipersTable() = JTable(snipers).apply {
    name = SNIPERS_TABLE_NAME
  }

  private fun fillContentPane(snipersTable: JTable) {
    contentPane.apply {
      add(JPanel().apply {
        layout = BorderLayout()
        add(JScrollPane(snipersTable), NORTH)
        add(JButton("Join").apply {
          name = SNIPER_JOIN_BUTTON_NAME
          addActionListener {
            val itemId = main.joinAuction()
            invokeLater {
              snipers.sniperStateChanged(SniperSnapshot(itemId, 0, 0, JOINING))
            }
          }
        }, SOUTH)
        pack()
      })
    }
  }

  companion object {
    const val MAIN_WINDOW_NAME: String = "Auction Sniper Name"
    const val SNIPERS_TABLE_NAME: String = "snipers table"
    const val SNIPER_JOIN_BUTTON_NAME: String = "sniper join button"
  }
}
