package goos.ui.stub

import goos.ui.api.Item
import goos.ui.api.MultiUserRequestListener
import goos.ui.api.PortfolioListener
import goos.ui.api.SniperListener
import goos.ui.api.SniperNotifier
import goos.ui.api.SniperSnapshot
import goos.ui.api.UI
import goos.ui.api.UserRequestListener
import goos.ui.api.stateText
import java.util.concurrent.atomic.AtomicBoolean

class StubUi : UI {

  @Volatile
  var itemField: String = ""
  @Volatile
  var stopPriceField: String = ""
  val started = AtomicBoolean(false)

  val columnTitles: List<String> = listOf("Item", "Last Price", "Last Bid", "State")
  val title: String = "Auction Sniper"

  val snipers = LinkedHashMap<String, SniperRow>()

  private val userRequestListeners = MultiUserRequestListener()

  override val portfolioListener: PortfolioListener = object : PortfolioListener {
    override fun sniperAdded(sniper: SniperNotifier) {
      sniper.addSniperListener(object : SniperListener {
        override fun sniperStateChanged(sniperSnapshot: SniperSnapshot) {
          snipers[sniperSnapshot.item.identifier] = SniperRow(
            itemId = sniperSnapshot.item.identifier,
            lastPrice = sniperSnapshot.lastPrice,
            lastBid = sniperSnapshot.lastBid,
            stateText = sniperSnapshot.stateText(),
          )
        }
      })
    }

    override fun reset() {
      snipers.clear()
    }
  }

  override fun addUserRequestListener(listener: UserRequestListener) {
    userRequestListeners.addListener(listener)
  }

  override fun start() {
    started.set(true)
  }

  fun clickResetButton() {
    userRequestListeners.reset()
  }

  fun clickBidButton() {
    userRequestListeners.joinAuction(Item(itemField, stopPriceField.toInt()))
  }
}

data class SniperRow(
  val itemId: String,
  val lastPrice: Int,
  val lastBid: Int,
  val stateText: String,
)
