package goos.auction.sol

import goos.auction.api.AuctionEventListener
import goos.auction.api.AuctionEventListener.PriceSource.FromOtherBidder
import goos.auction.api.AuctionEventListener.PriceSource.FromSniper
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.StringSpec
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify

class AuctionMessageTranslatorTest : StringSpec({

  val listener = mockk<AuctionEventListener>(relaxed = true)
  val translator = AuctionMessageTranslator(SNIPER_ID, listener)

  "notifies auction closed when close message received" {

    translator.processMessage("SOLVERSION: 1.1; Event: CLOSE;")

    verify(exactly = 1) {
      listener.auctionClosed()
    }
  }

  "notifies bid details when current price message received from other bidder" {

    translator.processMessage(
      "SOLVERSION: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;"
    )

    verify(exactly = 1) {
      listener.currentPrice(192, 7, FromOtherBidder)
    }
  }

  "notifies bid details when current price message received from sniper" {

    translator.processMessage(
      "SOLVERSION: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: $SNIPER_ID;"
    )

    verify(exactly = 1) {
      listener.currentPrice(192, 7, FromSniper)
    }
  }

  "notifies auction failed when bad message received" {

    translator.processMessage(
      "a bad message"
    )

    verify(exactly = 1) {
      listener.auctionFailed()
    }
  }

  "does nothing when unknown event received" {
    translator.processMessage("SOLVERSION: 1.1; Event: SOMETHING_NEW;")
  }

  afterTest {
    confirmVerified(listener)
  }
}) {
  override fun isolationMode() = InstancePerTest

  companion object {
    const val SNIPER_ID = "sniper"
  }
}
