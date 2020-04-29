package goos.core

import java.awt.Color.BLACK
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingUtilities.invokeLater
import javax.swing.border.LineBorder

class MainWindow(
  private val main: Main
) : JFrame("Auction Sniper") {

  private val sniperStatus = JLabel(STATUS_INITIAL).apply {
    name = SNIPER_STATUS_NAME
    border = LineBorder(BLACK)
  }

  private val sniperJoinButton = JButton("Join").apply {
    name = SNIPER_JOIN_BUTTON_NAME
    addActionListener {
      main.joinAuction()
      invokeLater {
        showStatus(STATUS_JOINING)
      }
    }
  }

  init {
    name = MAIN_WINDOW_NAME
    add(
      JPanel().apply {
        add(sniperStatus)
        add(sniperJoinButton)
        pack()
      }
    )
    pack()
    defaultCloseOperation = EXIT_ON_CLOSE
    isVisible = true
  }

  fun showStatus(status: String) {
    sniperStatus.text = status
  }

  companion object {
    const val MAIN_WINDOW_NAME: String = "Auction Sniper Name"
    const val SNIPER_STATUS_NAME: String = "sniper status"
    const val SNIPER_JOIN_BUTTON_NAME: String = "sniper join button"
    const val STATUS_LOST: String = "Lost"
    const val STATUS_BIDDING: String = "Bidding"
    const val STATUS_INITIAL: String = "Ready to join"
    const val STATUS_JOINING: String = "Joining"
  }
}
