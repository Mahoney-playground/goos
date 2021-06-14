package uk.org.lidalia.kotlinlangext.coroutines.sync

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

@ExperimentalTime
class CountDownLatchTest : StringSpec({

  "default initial count is 1" {
    CountDownLatch().count shouldBe 1
  }

  listOf(
    0,
    1,
    10,
    1_000_000
  ).forEach { initialCount ->
    "initial count $initialCount is allowed" {
      CountDownLatch(initialCount).count shouldBe initialCount
    }
  }

  listOf(
    -1,
    -1_000_000
  ).forEach { initialCount ->
    "initial count $initialCount is not allowed" {
      val e = shouldThrow<IllegalArgumentException> {
        CountDownLatch(initialCount)
      }
      e.message shouldBe "count $initialCount < 0"
    }
  }

  "countDown never goes below 0" {

    val n = 100 // number of coroutines to launch
    val k = 1000 // times an action is repeated by each coroutine

    val latch = CountDownLatch((n * k) / 2)
    inParallel(n, k) { latch.countDown() }

    latch.count shouldBe 0
  }

  "countDown is atomic" {

    val n = 100 // number of coroutines to launch
    val k = 1000 // times an action is repeated by each coroutine

    val latch = CountDownLatch((n * k) * 2)
    inParallel(n, k) { latch.countDown() }

    latch.count shouldBe n * k
  }

  "initial count 0 is open" {
    CountDownLatch(0).await()
  }

  "waits for countdown" {
    val latch = CountDownLatch()
    val events = mutableListOf<Int>()
    val job1 = launch {
      events.add(1)
      latch.await()
      events.add(3)
    }
    val job2 = launch {
      delay(milliseconds(100))
      events.add(2)
      latch.countDown()
    }
    joinAll(job1, job2)

    events shouldBe listOf(1, 2, 3)
  }

  "remains open forever" {
    val latch = CountDownLatch(1)
    val job = launch {
      inParallel(numberOfCoroutines = 100, repeatsPerCoroutine = 10) {
        latch.await()
        latch.await()
      }
    }
    delay(milliseconds(50))
    latch.countDown()
    job.join()
  }

  "returns true if await completes inside time" {
    val latch = CountDownLatch()
    val events = mutableListOf<Int>()
    val awaitedInTime = async {
      events.add(1)
      val inTime = latch.await(milliseconds(200))
      events.add(3)
      inTime
    }
    val job2 = launch {
      delay(milliseconds(100))
      events.add(2)
      latch.countDown()
    }
    joinAll(awaitedInTime, job2)

    awaitedInTime.await() shouldBe true
    events shouldBe listOf(1, 2, 3)
  }

  "returns false if await does not complete inside time" {
    val latch = CountDownLatch()
    val events = mutableListOf<Int>()
    val awaitedInTime = async {
      events.add(1)
      val inTime = latch.await(milliseconds(100))
      events.add(2)
      inTime
    }
    val job2 = launch {
      delay(milliseconds(200))
      events.add(3)
      latch.countDown()
    }
    joinAll(awaitedInTime, job2)

    awaitedInTime.await() shouldBe false
    events shouldBe listOf(1, 2, 3)
  }
})

suspend fun inParallel(
  numberOfCoroutines: Int = 100,
  repeatsPerCoroutine: Int = 1,
  block: suspend () -> Unit
) {
  withContext(Dispatchers.Default) {

    coroutineScope { // scope for coroutines
      repeat(numberOfCoroutines) {
        launch {
          repeat(repeatsPerCoroutine) { block() }
        }
      }
    }
  }
}
