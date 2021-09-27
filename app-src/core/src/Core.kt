package goos.core

import goos.auction.api.AuctionHouse
import goos.ui.api.UI
import uk.org.lidalia.kotlinlangext.concurrent.Signal
import uk.org.lidalia.kotlinlangext.threads.blockUntilShutdown

class Core(
  private val auctionHouse: AuctionHouse,
  private val ui: UI
) {

  fun run() {

    val portfolio = SniperPortfolio()
    val shutdownSignal = Signal.notTriggered()
    val sniperLauncher = SniperLauncher(
      auctionHouse,
      portfolio,
      shutdownSignal
    )

    ui.addUserRequestListener(sniperLauncher)
    portfolio.addPortfolioListener(ui.portfolioListener)

    ui.start()
    blockUntilShutdown(shutdownSignal)
  }
}
