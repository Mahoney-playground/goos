package goos.ui.swing

import io.kotest.core.Tag
import io.kotest.core.Tags
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.TagExtension

object UI : Tag()

object UITagExtension : TagExtension {
  private fun inDockerBuild() = true
  override fun tags(): Tags =
    if (inDockerBuild()) Tags.Empty else Tags.exclude(UI)
}

@Suppress("unused") // read by reflection
object ProjectConfig : AbstractProjectConfig() {
  override fun extensions() = listOf(UITagExtension)
}
