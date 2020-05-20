package goos.examples

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.threeten.extra.MutableClock
import java.time.Clock
import java.time.LocalDate
import java.time.temporal.ChronoUnit.HOURS

class Request

class Receiver(
  private var clock: Clock = Clock.systemDefaultZone()
) {

  private var dateOfFirstRequest: LocalDate? = null

  fun acceptRequest(request: Request): Boolean {
    val now = clock.instant().atZone(clock.zone).toLocalDate()
    if (dateOfFirstRequest == null) {
      dateOfFirstRequest = now
    }
    return firstDateIsDifferentFrom(now)
  }

  private fun firstDateIsDifferentFrom(now: LocalDate): Boolean {
    return dateOfFirstRequest == now
  }
}

class GoosTests : StringSpec({

  val clock = MutableClock.epochUTC()
  val receiver = Receiver(clock)

  val firstRequest = Request()
  val secondRequest = Request()

  "rejects requests not within the same day" {

    // given
    receiver.acceptRequest(firstRequest)

    // when
    clock.add(24, HOURS)

    // then
    receiver.acceptRequest(secondRequest) shouldBe false
  }
})
