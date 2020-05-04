package goos.core.ui

import goos.core.app.Main
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.NORTH
import java.awt.BorderLayout.SOUTH
import java.awt.Component
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.JTextField
import javax.swing.SwingUtilities

class MainWindow(
  private val main: Main,
  private val snipers: SnipersTableModel
) : JFrame("Auction Sniper") {

  init {
    name = MAIN_WINDOW_NAME
    fillContentPane(makeSnipersTable(), makeControls())
    pack()
    defaultCloseOperation = EXIT_ON_CLOSE
    isVisible = true
  }

  private fun makeSnipersTable() = JTable(snipers).apply {
    name = SNIPERS_TABLE_NAME
  }

  private fun makeControls() = JPanel(FlowLayout()).apply {
    add(JTextField().apply {
      columns = 25
      name = NEW_ITEM_ID_NAME
    })
    add(JButton("Join Auction").apply {
      name = JOIN_BUTTON_NAME
    })
  }

  private fun fillContentPane(snipersTable: JTable, controls: Component) {
    contentPane.apply {
      add(JPanel().apply {
        layout = BorderLayout()
        add(controls, NORTH)
        add(JScrollPane(snipersTable), CENTER)
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
    const val SNIPER_RESET_BUTTON_NAME: String = "sniper reset button"
    const val NEW_ITEM_ID_NAME: String = "new item id field"
    const val JOIN_BUTTON_NAME: String = "join button"
  }
}
