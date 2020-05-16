package goos

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.test.AssertionMode.Error
import io.kotest.core.test.TestCaseOrder.Random
import java.lang.Runtime.getRuntime

@Suppress("unused") // read by reflection
object ProjectConfig : AbstractProjectConfig() {
  override fun extensions() = listOf(UITagExtension)
  override val parallelism = getRuntime().availableProcessors()
  override val testCaseOrder = Random
  override val failOnIgnoredTests = true
  override val assertionMode = Error
}
