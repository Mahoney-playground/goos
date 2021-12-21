package goos.ui.swing

import io.kotest.core.Tag
import io.kotest.core.TagExpression
import io.kotest.core.TagExpression.Companion.Empty
import io.kotest.core.TagExpression.Companion.exclude
import io.kotest.core.extensions.TagExtension
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment

object UI : Tag()

object UITagExtension : TagExtension {

  override fun tags(): TagExpression =
    if (shouldRunUITests()) Empty else exclude(UI)

  private fun shouldRunUITests() = displayIsVirtual() || !runningAsPartOfBuild()

  // TODO somehow recognise this without hard coding a DISPLAY number
  // If you change this, change scripts/simple-xvfb-run.sh
  private fun displayIsVirtual(): Boolean =
    getLocalGraphicsEnvironment().screenDevices.any { it.isX11() } &&
      System.getenv("DISPLAY") == ":99"

  private fun GraphicsDevice.isX11() = this::class.java.simpleName.startsWith("X11")

  private fun runningAsPartOfBuild() = System.getenv("BUILD_SYSTEM") != null
}
