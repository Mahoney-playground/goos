package uk.org.lidalia.retry

import java.time.Clock
import java.time.Duration
import java.time.Instant
import arrow.core.Either
import kotlinx.coroutines.runBlocking

fun <A> retry(
  clock: Clock = Clock.systemUTC(),
  timeBetweenRetries: Duration = Duration.ofMillis(10),
  timeoutAfter: Duration = Duration.ofSeconds(10),
  work: () -> A
) = retry(clock, timeBetweenRetries, clock.instant().plus(timeoutAfter), work)

tailrec fun <A> retry(
  clock: Clock,
  timeBetweenRetries: Duration,
  timeout: Instant,
  work: () -> A
): A {
  val result = runBlocking {
    Either.catch {
      work.invoke()
    }
  }

  return when (result) {
    is Either.Left ->
      if (clock.instant().isAfter(timeout)) throw result.a
      else {
        Thread.sleep(timeBetweenRetries.toMillis())
        retry(
          timeBetweenRetries = timeBetweenRetries.multipliedBy(2),
          clock = clock,
          timeout = timeout,
          work = work
        )
      }
    is Either.Right -> result.b
  }
}
