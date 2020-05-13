package goos.core

import goos.common.Defect
import goos.core.api.SniperState.BIDDING
import goos.core.api.SniperState.JOINING
import goos.core.api.SniperState.LOST
import goos.core.api.SniperState.WINNING
import goos.core.api.SniperState.WON
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SniperStateTest : StringSpec({

  listOf(
    JOINING to LOST,
    BIDDING to LOST,
    WINNING to WON
  ).forEach { (initial, whenAuctionClosed) ->
    "$initial should become $whenAuctionClosed when closed" {
      initial.whenAuctionClosed() shouldBe whenAuctionClosed
    }
  }

  listOf(
    LOST,
    WON
  ).forEach { initial ->
    "$initial should not be closeable" {
      val defect = shouldThrow<Defect> {
        initial.whenAuctionClosed()
      }
      defect.message shouldBe "Auction is already closed"
    }
  }
})
