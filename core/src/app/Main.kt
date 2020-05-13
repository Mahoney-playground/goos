package goos.app

import goos.auction.xmpp.XMPPAuctionHouse
import goos.core.SniperLauncher
import goos.core.SniperPortfolio
import goos.ui.swing.buildUi
import uk.org.lidalia.kotlinlangext.threads.blockUntilShutdown
import java.util.logging.Level.WARNING
import java.util.logging.Logger

private const val ARG_HOSTNAME = 0
private const val ARG_USERNAME = 1
private const val ARG_PASSWORD = 2

fun main(vararg args: String) {

  Logger.getLogger("").level = WARNING

  println("Starting app")

  val auctionHouse = XMPPAuctionHouse(
    hostname = args[ARG_HOSTNAME],
    username = args[ARG_USERNAME],
    password = args[ARG_PASSWORD]
  )

  val sniperPortfolio = SniperPortfolio()
  val sniperLauncher = SniperLauncher(
    auctionHouse,
    sniperPortfolio
  )

  buildUi(sniperPortfolio, sniperLauncher)

  blockUntilShutdown()
  println("App stopping")
}
