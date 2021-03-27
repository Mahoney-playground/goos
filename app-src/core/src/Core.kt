package goos.core

import goos.auction.api.AuctionHouse
import goos.ui.api.UI
import uk.org.lidalia.kotlinlangext.concurrent.Gate
import uk.org.lidalia.kotlinlangext.threads.blockUntilShutdown

class Core(
  private val auctionHouse: AuctionHouse,
  private val ui: UI
) {

  fun run() {

    val portfolio = SniperPortfolio()
    val shutdownGate = Gate.closed()
    val sniperLauncher = SniperLauncher(
      auctionHouse,
      portfolio,
      shutdownGate
    )

    ui.addUserRequestListener(sniperLauncher)
    portfolio.addPortfolioListener(ui.portfolioListener)

    ui.start()
    blockUntilShutdown(shutdownGate)
  }
}
