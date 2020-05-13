package goos.ui.swing

import goos.ui.api.UserRequestListener
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

class MainWindow(
  private val snipers: SnipersTableModel
) : JFrame("Auction Sniper") {

  private val userRequests = MultiUserRequestListener()

  init {
    name = MAIN_WINDOW_NAME
    fillContentPane(makeSnipersTable(), makeControls(), makeConnectionControls())
    pack()
    defaultCloseOperation = EXIT_ON_CLOSE
    isVisible = true
  }

  private fun makeSnipersTable() = JTable(snipers).apply {
    name = SNIPERS_TABLE_NAME
  }

  private fun makeControls() = JPanel(FlowLayout()).apply {
    val itemIdField = JTextField().apply {
      columns = 25
      name = NEW_ITEM_ID_NAME
    }
    add(itemIdField)
    add(JButton("Join Auction").apply {
      name = JOIN_BUTTON_NAME
      addActionListener {
        userRequests.joinAuction(itemIdField.text)
      }
    })
  }

  private fun fillContentPane(
    snipersTable: JTable,
    controls: Component,
    connectionControls: Component
  ) {
    contentPane.apply {
      add(JPanel().apply {
        layout = BorderLayout()
        add(controls, NORTH)
        add(JScrollPane(snipersTable), CENTER)
        add(connectionControls, SOUTH)
        pack()
      })
    }
  }

  private fun makeConnectionControls() = JPanel(FlowLayout()).apply {
    add(JButton("Reset").apply {
      name = SNIPER_RESET_BUTTON_NAME
      addActionListener { userRequests.reset() }
    })
  }

  fun addUserRequestListener(userRequestListener: UserRequestListener) {
    userRequests.addListener(userRequestListener)
  }

  companion object {
    const val MAIN_WINDOW_NAME: String = "Auction Sniper Name"
    const val SNIPERS_TABLE_NAME: String = "snipers table"
    const val SNIPER_RESET_BUTTON_NAME: String = "sniper reset button"
    const val NEW_ITEM_ID_NAME: String = "new item id field"
    const val JOIN_BUTTON_NAME: String = "join button"
  }
}

private class MultiUserRequestListener : UserRequestListener {
  private val listeners = mutableListOf<UserRequestListener>()

  fun addListener(listener: UserRequestListener) {
    listeners.add(listener)
  }

  override fun joinAuction(itemId: String) {
    listeners.forEach { it.joinAuction(itemId) }
  }

  override fun reset() {
    listeners.forEach { it.reset() }
  }

  override fun disconnect() {
    listeners.forEach { it.disconnect() }
  }
}
