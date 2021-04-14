package uk.org.lidalia.retry

import arrow.core.Either
import kotlinx.coroutines.delay
import java.time.Clock
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds
import kotlin.time.seconds
import kotlin.time.toJavaDuration

@ExperimentalTime
suspend fun <A> retry(
  clock: Clock = Clock.systemUTC(),
  timeBetweenRetries: Duration = 10.milliseconds,
  timeoutAfter: Duration = 10.seconds,
  work: () -> A
) = retry(clock, timeBetweenRetries, clock.instant().plus(timeoutAfter.toJavaDuration()), work)

@ExperimentalTime
tailrec suspend fun <A> retry(
  clock: Clock,
  timeBetweenRetries: Duration,
  timeout: Instant,
  work: () -> A
): A =
  when (val result = Either.catch(work)) {
    is Either.Right<A> -> result.value
    is Either.Left<Throwable> ->
      if (timeout.inPast(clock)) throw result.value
      else {
        delay(timeBetweenRetries)
        retry(
          clock = clock,
          timeBetweenRetries = timeBetweenRetries * 2,
          timeout = timeout,
          work = work
        )
      }
  }

private fun Instant.inPast(clock: Clock) = clock.instant().isAfter(this)
