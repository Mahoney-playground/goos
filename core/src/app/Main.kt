package goos.app

import goos.auction.api.Auction
import goos.auction.xmpp.XMPPAuction
import goos.auction.xmpp.connection
import goos.core.AuctionSniper
import goos.ui.api.UiSniperSnapshot
import goos.ui.api.UserRequestListener
import goos.ui.swing.MainWindow
import goos.ui.swing.SnipersTableModel
import goos.ui.swing.SwingThreadSniperListener
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import uk.org.lidalia.kotlinlangext.threads.blockUntilShutdown
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.logging.Level.WARNING
import java.util.logging.Logger
import javax.swing.SwingUtilities

/*
 * The sniper user will not exist until the end to end tests create him!
 * It's very difficult to do it outside the end to end tests.
 *
 * Besides, it's better to have the app startup decoupled from the XMPP server.
 */
class Main(
  private val hostname: String,
  private val username: String,
  private val password: String
) {
  private var connection: XMPPTCPConnection? = null
  private val snipers = SnipersTableModel()
  private lateinit var ui: MainWindow
  private val notToBeGCd = mutableListOf<Auction>()

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

        snipers.addSniper(UiSniperSnapshot.joining(itemId))

        val auction = XMPPAuction(connection!!, itemId)
        notToBeGCd.add(auction)

        val sniper = AuctionSniper(
          itemId,
          auction,
          SwingThreadSniperListener(snipers)
        )

        auction.addAuctionEventListener(sniper)

        auction.join()
      }

      override fun reset() {
        snipers.reset()
      }

      override fun connect() {
        if (connection?.isConnected != true) {
          connection = connection(hostname, username, password)
        }
      }
    })
  }

  private fun disconnectWhenUICloses() =
    ui.addWindowListener(object : WindowAdapter() {
      override fun windowClosed(e: WindowEvent?) {
        connection?.disconnect()
      }
    })

  companion object {

    private const val ARG_HOSTNAME = 0
    private const val ARG_USERNAME = 1
    private const val ARG_PASSWORD = 2

    @JvmStatic
    fun main(vararg args: String) {

      Logger.getLogger("").level = WARNING

      println("Starting app")
      Main(
        hostname = args[ARG_HOSTNAME],
        username = args[ARG_USERNAME],
        password = args[ARG_PASSWORD]
      )
      blockUntilShutdown()
      println("App stopping")
    }
  }
}
