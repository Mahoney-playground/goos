package goos.core

import java.util.concurrent.CountDownLatch
import javax.swing.JButton

import javax.swing.JFrame

object Main {

  const val SNIPER_STATUS_NAME: String = "SNIPER_STATUS_NAME"

  const val STATUS_LOST: String = "Lost"
  const val STATUS_JOINING: String = "Joining"

  @JvmStatic
  fun main(vararg args: String) {
    println("Starting app")
    val frame = JFrame("My First GUI")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.setSize(300, 300)
    val button = JButton("Press")
    frame.contentPane.add(button) // Adds Button to content pane of frame

    frame.isVisible = true
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
