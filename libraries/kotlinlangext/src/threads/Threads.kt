package uk.org.lidalia.kotlinlangext.threads

import uk.org.lidalia.kotlinlangext.concurrent.Gate

fun blockUntilShutdown(
  gate: Gate = Gate.closed()
) {
  val runningThread = Thread.currentThread()

  Runtime.getRuntime().addShutdownHook(
    object : Thread() {
      override fun run() {
        gate.open()
        runningThread.join()
      }
    }
  )

  gate.waitUntilOpened()
}
