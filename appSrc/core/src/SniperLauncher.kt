package goos.core

import goos.auction.api.AuctionHouse
import goos.portfolio.Item
import goos.ui.api.UserRequestListener
import java.util.concurrent.CountDownLatch

internal class SniperLauncher(
  private val auctionHouse: AuctionHouse,
  private val portfolio: SniperPortfolio,
  private val latch: CountDownLatch
) : UserRequestListener {

  override fun joinAuction(item: Item) {

    val auction = auctionHouse.auctionFor(item.identifier)

    val sniper = AuctionSniper(item, auction)

    auction.addAuctionEventListener(sniper)

    portfolio.addSniper(sniper)

    auction.join()
  }

  override fun reset() {
    portfolio.reset()
  }

  override fun closeApplication() {
    auctionHouse.disconnect()
    latch.countDown()
  }
}
