package uk.org.lidalia.retry

import arrow.core.Either
import kotlinx.coroutines.delay
import java.time.Clock
import java.time.Duration
import java.time.Instant

suspend fun <A> retry(
  clock: Clock = Clock.systemUTC(),
  timeBetweenRetries: Duration = Duration.ofMillis(10),
  timeoutAfter: Duration = Duration.ofSeconds(10),
  work: suspend () -> A
) = retry(clock, timeBetweenRetries, clock.instant().plus(timeoutAfter), work)

tailrec suspend fun <A> retry(
  clock: Clock,
  timeBetweenRetries: Duration,
  timeout: Instant,
  work: suspend () -> A
): A =
  when (val result = Either.catch(work)) {
    is Either.Right -> result.b
    is Either.Left ->
      if (timeout.inPast(clock)) throw result.a
      else {
        delay(timeBetweenRetries.toMillis())
        retry(
          clock = clock,
          timeBetweenRetries = timeBetweenRetries.multipliedBy(2),
          timeout = timeout,
          work = work
        )
      }
  }

private fun Instant.inPast(clock: Clock) = clock.instant().isAfter(this)
