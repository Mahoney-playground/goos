package goos.core

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
  private val main: Main
) : JFrame("Auction Sniper") {

  private val snipers = SnipersTableModel()

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
            main.joinAuction()
            invokeLater {
              showStatus(STATE_JOINING)
            }
          }
        }, SOUTH)
        pack()
      })
    }
  }

  fun showStatus(status: String) {
    snipers.stateText = status
  }

  fun sniperStatusChanged(snapshot: SniperSnapshot) {
    snipers.sniperStatusChanged(snapshot)
  }

  companion object {
    const val MAIN_WINDOW_NAME: String = "Auction Sniper Name"
    const val SNIPERS_TABLE_NAME: String = "snipers table"
    const val SNIPER_JOIN_BUTTON_NAME: String = "sniper join button"
    const val STATE_LOST: String = "Lost"
    const val STATE_BIDDING: String = "Bidding"
    const val STATE_INITIAL: String = "Ready to join"
    const val STATE_JOINING: String = "Joining"
    const val STATE_WINNING: String = "Winning"
    const val STATE_WON: String = "Won"
  }
}
