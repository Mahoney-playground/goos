package goos.core

import goos.common.Defect
import goos.core.SniperState.BIDDING
import goos.core.SniperState.JOINING
import goos.core.SniperState.LOSING
import goos.core.SniperState.LOST
import goos.core.SniperState.WINNING
import goos.core.SniperState.WON
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SniperStateTest : StringSpec({

  listOf(
    JOINING to LOST,
    BIDDING to LOST,
    WINNING to WON,
    LOSING to LOST
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
