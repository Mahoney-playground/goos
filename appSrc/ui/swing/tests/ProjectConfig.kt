package goos.ui.swing

import io.kotest.core.config.AbstractProjectConfig

@Suppress("unused") // read by reflection
object ProjectConfig : AbstractProjectConfig() {
  override fun extensions() = listOf(UITagExtension)
}
