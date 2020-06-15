package goos.app

import goos.auction.xmpp.XMPPAuctionHouse
import goos.core.Core
import goos.ui.swing.MainWindow
import java.util.logging.Level.WARNING
import java.util.logging.Logger

private const val ARG_HOSTNAME = 0
private const val ARG_USERNAME = 1
private const val ARG_PASSWORD = 2

fun main(vararg args: String) {

  Logger.getLogger("").level = WARNING

  println("Starting the app")

  val auctionHouse = XMPPAuctionHouse(
    hostname = args[ARG_HOSTNAME],
    username = args[ARG_USERNAME],
    password = args[ARG_PASSWORD]
  )

  val ui = MainWindow()

  val core = Core(auctionHouse, ui)

  core.start()
}
