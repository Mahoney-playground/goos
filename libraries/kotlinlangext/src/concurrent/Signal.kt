package uk.org.lidalia.kotlinlangext.concurrent

import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.NANOSECONDS

/**
 * Specialization of CountDownLatch which can only be triggered
 */
class Signal private constructor() {

  private val latch = CountDownLatch(1)

  @Throws(InterruptedException::class)
  fun await(duration: Duration? = null): Boolean = if (duration != null) {
    latch.await(duration.toNanos(), NANOSECONDS)
  } else {
    latch.await()
    true
  }

  fun trigger(): Unit = latch.countDown()

  val isTriggered: Boolean get() = latch.count == 0L

  companion object {
    fun notTriggered() = Signal()
    val triggered = Signal().trigger()
  }
}
