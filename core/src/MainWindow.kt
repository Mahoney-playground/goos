package goos.core

import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode.disabled
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.chat.ChatManager
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import java.awt.Color.BLACK
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingUtilities.invokeLater
import javax.swing.border.LineBorder

class MainWindow(
  private val hostname: String,
  private val username: String,
  private val password: String,
  private val itemId: String
) : JFrame("Auction Sniper") {

  private var connection: XMPPConnection? = null

  private val sniperStatus = JLabel(STATUS_INITIAL).apply {
    name = SNIPER_STATUS_NAME
    border = LineBorder(BLACK)
  }

  private val sniperJoinButton = JButton("Join").apply {
    name = SNIPER_JOIN_BUTTON_NAME
    addActionListener {
      joinAuction()
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

  private fun joinAuction() {
    if (connection == null) {
      connection = connectTo(
        hostname,
        username,
        password
      )
    }
    val chat = ChatManager.getInstanceFor(connection)
      .createChat(auctionId(itemId, hostname)) { _, _ ->
        invokeLater {
          showStatus(STATUS_LOST)
        }
      }
    chat.sendMessage(Message())
  }

  fun showStatus(status: String) {
    sniperStatus.text = status
  }

  companion object {
    const val MAIN_WINDOW_NAME: String = "Auction Sniper Name"
    const val SNIPER_STATUS_NAME: String = "sniper status"
    const val SNIPER_JOIN_BUTTON_NAME: String = "sniper join button"
    const val STATUS_LOST: String = "Lost"
    const val STATUS_INITIAL: String = "Ready to join"
    const val STATUS_JOINING: String = "Joining"
    private val AUCTION_RESOURCE = Resourcepart.from("Auction")

    private fun connectTo(
      hostname: String,
      username: String,
      password: String
    ): XMPPConnection {
      val connection = XMPPTCPConnection(
        XMPPTCPConnectionConfiguration.builder()
          .setSecurityMode(disabled)
          .setXmppDomain(JidCreate.domainBareFrom(
            hostname
          ))
          .build()
      )
      .connect()
      connection.login(username, password, AUCTION_RESOURCE)
      return connection
    }

    private fun auctionId(itemId: String, hostname: String): EntityBareJid =
      JidCreate.entityBareFrom("auction-$itemId@$hostname/$AUCTION_RESOURCE")
  }
}
