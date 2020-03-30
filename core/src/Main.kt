package goos.core

import java.util.concurrent.CountDownLatch

object Main {

  const val SNIPER_STATUS_NAME: String = "SNIPER_STATUS_NAME"

  const val STATUS_LOST: String = "Lost"
  const val STATUS_JOINING: String = "Joining"

  @JvmStatic
  fun main(vararg args: String) {
    println("Starting app")
      blockUntilShutdown()
    println("App stopping")
  }

  private fun blockUntilShutdown() {
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
}
