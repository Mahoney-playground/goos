package uk.org.lidalia.kotlinlangext.threads

import uk.org.lidalia.kotlinlangext.concurrent.Signal

fun blockUntilShutdown(
  shutdownSignal: Signal = Signal.notTriggered(),
) {
  val runningThread = Thread.currentThread()

  Runtime.getRuntime().addShutdownHook {
    shutdownSignal.trigger()
    runningThread.join()
  }

  shutdownSignal.await()
}

fun Runtime.addShutdownHook(work: () -> Unit) = addShutdownHook(Thread(work))
