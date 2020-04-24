package goos.core

import java.util.concurrent.CountDownLatch
import javax.swing.SwingUtilities

class Main(
  private val hostname: String,
  private val username: String,
  private val password: String,
  private val itemId: String
) {

  private lateinit var ui: MainWindow

  init {
    startUserInterface()
  }

  private fun startUserInterface() {
    SwingUtilities.invokeAndWait {
      ui = MainWindow(
        hostname = hostname,
        username = username,
        password = password,
        itemId = itemId
      )
    }
  }

  companion object {

    private const val ARG_HOSTNAME = 0
    private const val ARG_USERNAME = 1
    private const val ARG_PASSWORD = 2
    private const val ARG_ITEM_ID = 3

    @JvmStatic
    fun main(vararg args: String) {
      println("Starting app")
      Main(
        hostname = args[ARG_HOSTNAME],
        username = args[ARG_USERNAME],
        password = args[ARG_PASSWORD],
        itemId = args[ARG_ITEM_ID]
      )
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
}
