package goos.core

import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.chat.Chat
import org.jivesoftware.smack.chat.ChatManager
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.concurrent.CountDownLatch
import javax.swing.SwingUtilities

class Main(
  private val hostname: String,
  private val username: String,
  private val password: String,
  private val itemId: String
) : SniperListener {

  private lateinit var ui: MainWindow
  private var connection: XMPPTCPConnection? = null
  private var notToBeGCd: Chat? = null

  init {
    startUserInterface()
  }

  private fun startUserInterface() {
    SwingUtilities.invokeAndWait {
      ui = MainWindow(this)
    }
  }

  internal fun joinAuction() {
    connect()

    disconnectWhenUICloses(connection!!)

    val chat = ChatManager.getInstanceFor(connection)
      .createChat(
        auctionId(itemId, hostname),
        null
      )
    notToBeGCd = chat

    val auction = object : Auction {
      override fun bid(bid: Int) {
        chat.sendMessage("SOLVersion: 1.1; Command: BID; Price: $bid;")
      }
    }
    chat.addMessageListener(AuctionMessageTranslator(AuctionSniper(auction, this)))

    chat.sendMessage("SOLVersion: 1.1; Command: JOIN")
  }

  private fun connect() {
    if (connection == null) {
      connection = connectTo(
        hostname,
        username,
        password
      )
    }
  }

  private fun disconnectWhenUICloses(connection: XMPPTCPConnection) {
    ui.addWindowListener(object : WindowAdapter() {
      override fun windowClosed(e: WindowEvent?) {
        connection.disconnect()
      }
    })
  }

  override fun sniperLost() {
    SwingUtilities.invokeLater {
      ui.showStatus(MainWindow.STATUS_LOST)
    }
  }

  override fun sniperBidding() {
    SwingUtilities.invokeLater {
      ui.showStatus(MainWindow.STATUS_BIDDING)
    }
  }

  companion object {

    private const val ARG_HOSTNAME = 0
    private const val ARG_USERNAME = 1
    private const val ARG_PASSWORD = 2
    private const val ARG_ITEM_ID = 3

    private val AUCTION_RESOURCE = Resourcepart.from("Auction")

    @JvmStatic
    fun main(vararg args: String) {
      println("Starting app")
      Main(
        hostname = args[ARG_HOSTNAME],
        username = args[ARG_USERNAME],
        password = args[ARG_PASSWORD],
        itemId = args[ARG_ITEM_ID]
      )
      blockUntilShutdown()
      println("App stopping")
    }

    private fun blockUntilShutdown() {
      val latch = CountDownLatch(1)
      val runningThread = Thread.currentThread()

      Runtime.getRuntime().addShutdownHook(
        object : Thread() {
          override fun run() {
            latch.countDown()
            runningThread.join()
          }
        }
      )

      latch.await()
    }

    private fun connectTo(
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
