package goos.core

import goos.core.AuctionEventListener.PriceSource.FromOtherBidder
import goos.core.AuctionEventListener.PriceSource.FromSniper
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.StringSpec
import io.mockk.mockk
import io.mockk.verify
import org.jivesoftware.smack.packet.Message

class AuctionMessageTranslatorTest : StringSpec({

  val listener = mockk<AuctionEventListener>(relaxed = true)
  val translator = AuctionMessageTranslator(SNIPER_ID, listener)

  "notifies auction closed when close message received" {

    val message = Message().apply {
      body = "SOLVERSION: 1.1; Event: CLOSE;"
    }

    translator.processMessage(null, message)

    verify(exactly = 1) {
      listener.auctionClosed()
    }
  }

  "notifies bid details when current price message received from other bidder" {

    val message = Message().apply {
      body = "SOLVERSION: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;"
    }

    translator.processMessage(null, message)

    verify(exactly = 1) {
      listener.currentPrice(192, 7, FromOtherBidder)
    }
  }

  "notifies bid details when current price message received from sniper" {

    val message = Message().apply {
      body = "SOLVERSION: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: $SNIPER_ID;"
    }

    translator.processMessage(null, message)

    verify(exactly = 1) {
      listener.currentPrice(192, 7, FromSniper)
    }
  }
}) {
  override fun isolationMode() = InstancePerTest

  companion object {
    const val SNIPER_ID = "sniper"
  }
}
