package goos.core

import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.StringSpec
import io.mockk.mockk
import io.mockk.verify
import org.jivesoftware.smack.packet.Message

class AuctionMessageTranslatorTest : StringSpec({

  val listener = mockk<AuctionEventListener>(relaxed = true)
  val translator = AuctionMessageTranslator(listener)

  "notifies auction closed when close message received" {

    val message = Message().apply {
      body = "SOLVERSION: 1.1; Event: CLOSE;"
    }

    translator.processMessage(null, message)

    verify(exactly = 1) {
      listener.auctionClosed()
    }
  }

  "notifies bid details when current price message received" {

    val message = Message().apply {
      body = "SOLVERSION: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;"
    }

    translator.processMessage(null, message)

    verify(exactly = 1) {
      listener.currentPrice(192, 7)
    }
  }
}) {
  override fun isolationMode() = InstancePerTest
}
