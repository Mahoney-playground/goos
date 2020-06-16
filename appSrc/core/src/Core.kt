package goos.core

import goos.auction.api.AuctionHouse
import goos.ui.api.UI
import uk.org.lidalia.kotlinlangext.threads.blockUntilShutdown
import java.util.concurrent.CountDownLatch

class Core(
  auctionHouse: AuctionHouse,
  private val ui: UI
) {

  private val latch = CountDownLatch(1)

  init {
    val portfolio = SniperPortfolio()
    val sniperLauncher = SniperLauncher(auctionHouse, portfolio, latch)

    ui.addUserRequestListener(sniperLauncher)
    portfolio.addPortfolioListener(ui.portfolioListener)
  }

  fun run() {
    ui.start()
    blockUntilShutdown(latch)
    println("App stopping")
  }
}
