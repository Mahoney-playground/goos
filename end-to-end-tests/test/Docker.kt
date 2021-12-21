package goos

import io.kotest.core.Tag
import io.kotest.core.TagExpression
import io.kotest.core.TagExpression.Companion.Empty
import io.kotest.core.TagExpression.Companion.exclude
import io.kotest.core.extensions.TagExtension

object Docker : Tag()

object DockerTagExtension : TagExtension {

  override fun tags(): TagExpression =
    if (shouldRunDockerTests()) Empty else exclude(Docker)

  private fun shouldRunDockerTests() = !runningAsPartOfBuild()

  private fun runningAsPartOfBuild() = System.getenv("BUILD_SYSTEM") != null
}
