package goos.app

import goos.auction.xmpp.XMPPAuctionHouse
import goos.core.SniperLauncher
import goos.ui.swing.MainWindow
import goos.ui.swing.SnipersTableModel
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
  private val snipers = SnipersTableModel()
  private val sniperLauncher = SniperLauncher(
    XMPPAuctionHouse(
      hostname = hostname,
      username = username,
      password = password
    ),
    snipers
  )
  private lateinit var ui: MainWindow

  init {
    startUserInterface()
    disconnectWhenUICloses()
    addUserRequestListener()
  }

  private fun startUserInterface() = SwingUtilities.invokeAndWait {
    ui = MainWindow(snipers)
  }

  private fun addUserRequestListener() {
    ui.addUserRequestListener(sniperLauncher)
  }

  private fun disconnectWhenUICloses() =
    ui.addWindowListener(object : WindowAdapter() {
      override fun windowClosed(e: WindowEvent?) {
        sniperLauncher.disconnect()
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
