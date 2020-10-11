package goos

import io.kotest.core.Tag
import io.kotest.core.Tags
import io.kotest.core.extensions.TagExtension

object Docker : Tag()

object DockerTagExtension : TagExtension {

  override fun tags(): Tags =
    if (shouldRunDockerTests()) Tags.Empty else Tags.exclude(Docker)

  private fun shouldRunDockerTests() = !runningAsPartOfBuild()

  private fun runningAsPartOfBuild() = System.getenv("BUILD_SYSTEM") != null
}
