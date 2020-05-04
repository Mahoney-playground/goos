package goos.core.app

import goos.core.auction.xmpp.AuctionMessageTranslator
import goos.core.auction.xmpp.XMPPAuction
import goos.core.core.AuctionSniper
import goos.core.core.SniperSnapshot
import goos.core.ui.MainWindow
import goos.core.ui.SnipersTableModel
import goos.core.ui.SwingThreadSniperListener
import goos.core.ui.api.UserRequestListener
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.chat.Chat
import org.jivesoftware.smack.chat.ChatManager
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import uk.org.lidalia.kotlinlangext.threads.blockUntilShutdown
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.logging.Level.WARNING
import java.util.logging.Logger
import javax.swing.SwingUtilities

class Main(
  private val connection: XMPPTCPConnection
) {

  private val snipers = SnipersTableModel()
  private lateinit var ui: MainWindow
  private val notToBeGCd = mutableListOf<Chat>()

  init {
    startUserInterface()
    disconnectWhenUICloses()
    addUserRequestListener()
  }

  private fun startUserInterface() = SwingUtilities.invokeAndWait {
    ui = MainWindow(snipers)
  }

  private fun addUserRequestListener() {
    ui.addUserRequestListener(object : UserRequestListener {
      override fun joinAuction(itemId: String) {
        snipers.addSniper(SniperSnapshot.joining(itemId))

        val chat = ChatManager.getInstanceFor(connection)
          .createChat(
            auctionId(itemId, connection.host),
            null
          )
        notToBeGCd.add(chat)

        val auction = XMPPAuction(chat)
        chat.addMessageListener(AuctionMessageTranslator(
          connection.user.toString(),
          AuctionSniper(
            itemId,
            auction,
            SwingThreadSniperListener(snipers)
          )
        ))
        auction.join()
      }

      override fun reset() {
        snipers.reset()
      }
    })
  }

  private fun disconnectWhenUICloses() =
    ui.addWindowListener(object : WindowAdapter() {
      override fun windowClosed(e: WindowEvent?) {
        connection.disconnect()
      }
    })

  companion object {

    private const val ARG_HOSTNAME = 0
    private const val ARG_USERNAME = 1
    private const val ARG_PASSWORD = 2

    private val AUCTION_RESOURCE = Resourcepart.from("Auction")

    @JvmStatic
    fun main(vararg args: String) {

      Logger.getLogger("").level = WARNING

      println("Starting app")
      val connection = connection(
        hostname = args[ARG_HOSTNAME],
        username = args[ARG_USERNAME],
        password = args[ARG_PASSWORD]
      )
      Main(connection)
      blockUntilShutdown()
      println("App stopping")
    }

    private fun connection(
      hostname: String,
      username: String,
      password: String
    ): XMPPTCPConnection {
      val connection = XMPPTCPConnection(
        XMPPTCPConnectionConfiguration.builder()
          .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
          .setXmppDomain(
            JidCreate.domainBareFrom(
              hostname
            )
          )
          .build()
      )
      connection.connect()
      connection.login(username, password, AUCTION_RESOURCE)
      return connection
    }

    private fun auctionId(itemId: String, hostname: String): EntityBareJid =
      JidCreate.entityBareFrom("auction-$itemId@$hostname/$AUCTION_RESOURCE")
  }
}
