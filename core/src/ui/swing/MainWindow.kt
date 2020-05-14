package goos.ui.swing

import goos.core.MultiUserRequestListener
import goos.core.PortfolioNotifier
import goos.core.UserRequestListener
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.NORTH
import java.awt.BorderLayout.SOUTH
import java.awt.Component
import java.awt.FlowLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.text.NumberFormat
import javax.swing.JButton
import javax.swing.JFormattedTextField
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.JTextField

class MainWindow(
  portfolio: PortfolioNotifier
) : JFrame("Auction Sniper") {

  private val userRequests = MultiUserRequestListener()

  init {
    name = MAIN_WINDOW_NAME
    fillContentPane(makeSnipersTable(portfolio), makeControls(), makeConnectionControls())
    pack()
    defaultCloseOperation = EXIT_ON_CLOSE
    isVisible = true
    addWindowListener(object : WindowAdapter() {
      override fun windowClosing(e: WindowEvent) {
        userRequests.disconnect()
      }
    })
  }

  private fun makeSnipersTable(
    portfolio: PortfolioNotifier
  ): JTable {

    val model = SnipersTableModel()
    portfolio.addPortfolioListener(model)

    return JTable(model).apply {
      name = SNIPERS_TABLE_NAME
    }
  }

  private fun makeControls() = JPanel(FlowLayout()).apply {

    val itemIdField = JTextField().apply {
      columns = 25
      name = NEW_ITEM_ID_NAME
    }

    val stopPriceField = JFormattedTextField(NumberFormat.getNumberInstance()).apply {
      columns = 10
      name = NEW_ITEM_STOP_PRICE_NAME
    }

    add(JLabel("Item:").apply {
      labelFor = itemIdField
    })
    add(itemIdField)

    add(JLabel("Stop price:").apply {
      labelFor = stopPriceField
    })
    add(stopPriceField)

    add(JButton("Join Auction").apply {
      name = JOIN_BUTTON_NAME
      addActionListener {
        userRequests.joinAuction(itemIdField.text, (stopPriceField.value as Long).toInt())
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
    const val NEW_ITEM_STOP_PRICE_NAME: String = "new item stop price field"
    const val JOIN_BUTTON_NAME: String = "join button"
  }
}
