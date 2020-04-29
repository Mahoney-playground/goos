package uk.org.lidalia.kotlinlangext.threads

import java.util.concurrent.CountDownLatch

fun blockUntilShutdown() {
  val latch = CountDownLatch(1)
  val runningThread = Thread.currentThread()

  Runtime.getRuntime().addShutdownHook(
    object : Thread() {
      override fun run() {
        latch.countDown()
        runningThread.join()
      }
    }
  )

  latch.await()
}
