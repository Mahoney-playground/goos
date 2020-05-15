package uk.org.lidalia.kotlinlangext.coroutines.sync

import kotlinx.coroutines.sync.Mutex
import java.lang.IllegalStateException
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class CountDownLatch private constructor(
  private val mutex: Mutex,
  private val counter: AtomicInteger
) {

  init {
    maybeUnlock()
  }

  fun countDown() {
    counter.updateAndGet { if (it == 0) 0 else it - 1 }
    maybeUnlock()
  }

  private fun maybeUnlock() {
    if (counter.get() == 0 && mutex.isLocked) {
      swallow<IllegalStateException> {
        mutex.unlock()
      }
    }
  }

  suspend fun await() {
    if (mutex.isLocked) {
      mutex.lock()
      mutex.unlock()
    }
  }

  @ExperimentalTime
  suspend fun await(until: Duration): Boolean {
    if (mutex.isLocked) {
      mutex.lock()
      mutex.unlock()
    }
    return true
  }

  val count: Int
    get() = counter.get()

  override fun toString(): String = "${super.toString()}[Count = $counter]"

  companion object {
    suspend operator fun invoke(count: Int = 1): CountDownLatch {
      require(count >= 0) { "count $count < 0" }
      val mutex = Mutex()
      mutex.lock()
      return CountDownLatch(mutex, AtomicInteger(count))
    }
  }
}

inline fun <reified T : Throwable> swallow(block: () -> Unit) {
  try {
    block()
  } catch (t: Throwable) {
    if (t !is T) {
      throw t
    }
  }
}
