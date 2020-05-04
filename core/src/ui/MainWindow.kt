package goos.core.ui

import goos.core.app.Main
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.NORTH
import java.awt.BorderLayout.SOUTH
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.SwingUtilities

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
            SwingUtilities.invokeLater {
              main.joinAuctions()
            }
          }
        }, CENTER)
        add(JButton("Reset").apply {
          name = SNIPER_RESET_BUTTON_NAME
          addActionListener {
            SwingUtilities.invokeLater {
              main.reset()
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
    const val SNIPER_RESET_BUTTON_NAME: String = "sniper reset button"
  }
}
