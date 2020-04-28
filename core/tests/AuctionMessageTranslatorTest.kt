package goos.core

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
})
