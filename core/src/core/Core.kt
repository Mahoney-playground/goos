package goos.core

import goos.auction.api.AuctionHouse

class Core(
  auctionHouse: AuctionHouse
) {
  private val portfolio = SniperPortfolio()
  val portfolioNotifier: PortfolioNotifier = portfolio
  val userRequestListener: UserRequestListener = SniperLauncher(auctionHouse, portfolio)
}
