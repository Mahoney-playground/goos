package uk.org.lidalia.gradle.plugins.copywithoutversion

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.gradle.api.attributes.Attribute
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

class CopyWithoutVersionPlugin : Plugin<Project> {
  override fun apply(project: Project) {}
}

abstract class CopyWithoutVersionsTask : DefaultTask() {

  @InputFiles
  lateinit var from: Configuration

  @OutputDirectory
  lateinit var into: File

  @TaskAction
  fun copyWithoutVersions() {
    project.copy {
      from.incoming.artifacts.forEach { artifact ->
        from(artifact.file)
        into(into)
        rename { originalName ->
          artifact.filenameWithoutVersion() ?: originalName
        }
      }
    }
  }
}

private val artifactType = Attribute.of("artifactType", String::class.java)

private fun ResolvedArtifactResult.filenameWithoutVersion(): String? {
  @Suppress("UnstableApiUsage")
  val owner = variant.owner
  val type = variant.attributes.getAttribute(artifactType)
  return if (owner is ModuleComponentIdentifier && type != null) {
    "${owner.module}.$type"
  } else {
    null
  }
}
