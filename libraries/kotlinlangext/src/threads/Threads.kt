package uk.org.lidalia.kotlinlangext.threads

import uk.org.lidalia.kotlinlangext.concurrent.Signal

fun blockUntilShutdown(
  shutdownSignal: Signal = Signal.notTriggered()
) {
  val runningThread = Thread.currentThread()

  Runtime.getRuntime().addShutdownHook(
    object : Thread() {
      override fun run() {
        shutdownSignal.trigger()
        runningThread.join()
      }
    }
  )

  shutdownSignal.await()
}
