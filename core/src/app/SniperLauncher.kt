package goos.app

import goos.auction.api.Auction
import goos.auction.api.AuctionHouse
import goos.auction.xmpp.XMPPAuctionHouse
import goos.core.AuctionSniper
import goos.ui.api.UiSniperSnapshot
import goos.ui.api.UserRequestListener
import goos.ui.swing.SnipersTableModel
import goos.ui.swing.SwingThreadSniperListener

class SniperLauncher(
  private val hostname: String,
  private val username: String,
  private val password: String,
  private val snipers: SnipersTableModel
) : UserRequestListener {

  private var auctionHouse: AuctionHouse? = null
  private val notToBeGCd = mutableListOf<Auction>()

  override fun joinAuction(itemId: String) {

    snipers.addSniper(UiSniperSnapshot.joining(itemId))

    val auction = auctionHouse!!.auctionFor(itemId)
    notToBeGCd.add(auction)

    val sniper = AuctionSniper(
        itemId,
        auction,
        SwingThreadSniperListener(snipers)
    )

    auction.addAuctionEventListener(sniper)

    auction.join()
  }

  override fun reset() {
    snipers.reset()
  }

  override fun connect() {
    if (auctionHouse == null) {
      auctionHouse = XMPPAuctionHouse.connect(hostname, username, password)
    }
  }

  override fun disconnect() {
    auctionHouse?.disconnect()
  }
}
