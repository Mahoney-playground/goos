package uk.org.lidalia.retry

import java.time.Clock
import java.time.Duration
import java.time.Instant

fun <A> retry(
  clock: Clock = Clock.systemUTC(),
  timeBetweenRetries: Duration = Duration.ofMillis(10),
  timeoutAfter: Duration = Duration.ofSeconds(10),
  work: () -> A
) = retry(clock, timeBetweenRetries, clock.instant().plus(timeoutAfter), work)

fun <A> retry(
  clock: Clock,
  timeBetweenRetries: Duration,
  timeout: Instant,
  work: () -> A
): A {
  return try {
    work.invoke()
  } catch (e: Exception) {
    if (clock.instant().isAfter(timeout)) throw e
    else {
      Thread.sleep(timeBetweenRetries.toMillis())
      retry(
        timeBetweenRetries = timeBetweenRetries.multipliedBy(2),
        clock = clock,
        timeout = timeout,
        work = work
      )
    }
  }
}
