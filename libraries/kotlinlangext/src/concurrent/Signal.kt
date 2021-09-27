package uk.org.lidalia.kotlinlangext.concurrent

import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.NANOSECONDS

/**
 * Specialization of CountDownLatch with can only be opened
 */
class Gate private constructor() {

  private val latch = CountDownLatch(1)

  @Throws(InterruptedException::class)
  fun waitUntilOpened(): Unit = latch.await()

  @Throws(InterruptedException::class)
  fun waitUntilOpened(duration: Duration): Boolean = latch.await(duration.toNanos(), NANOSECONDS)

  fun open(): Unit = latch.countDown()

  fun isOpen(): Boolean = latch.count > 0
  fun isClosed(): Boolean = !isOpen()

  companion object {
    fun closed() = Gate()
  }
}
