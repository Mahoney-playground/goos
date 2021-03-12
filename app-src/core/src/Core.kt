package goos.core

import goos.auction.api.AuctionHouse
import goos.ui.api.UI
import uk.org.lidalia.kotlinlangext.threads.blockUntilShutdown
import java.util.concurrent.CountDownLatch

class Core(
  private val auctionHouse: AuctionHouse,
  private val ui: UI
) {

  fun run() {

    val portfolio = SniperPortfolio()
    val latch = CountDownLatch(1)
    val sniperLauncher = SniperLauncher(
      auctionHouse,
      portfolio,
      latch
    )

    ui.addUserRequestListener(sniperLauncher)
    portfolio.addPortfolioListener(ui.portfolioListener)

    ui.start()
    blockUntilShutdown(latch)
  }
}
