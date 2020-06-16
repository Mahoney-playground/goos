package goos.core

import goos.auction.api.AuctionHouse
import goos.ui.api.UI
import uk.org.lidalia.kotlinlangext.threads.blockUntilShutdown

class Core(
  auctionHouse: AuctionHouse,
  private val ui: UI
) {

  init {
    val portfolio = SniperPortfolio()
    val sniperLauncher = SniperLauncher(auctionHouse, portfolio)

    ui.addUserRequestListener(sniperLauncher)
    portfolio.addPortfolioListener(ui.portfolioListener)
  }

  fun run() {
    ui.start()
    blockUntilShutdown()
    println("App stopping")
  }
}
