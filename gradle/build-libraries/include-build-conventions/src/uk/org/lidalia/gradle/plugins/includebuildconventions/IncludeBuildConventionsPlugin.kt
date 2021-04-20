@file:Suppress("unused")

package uk.org.lidalia.gradle.plugins.includebuildconventions

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import java.io.File

class IncludeBuildConventionsPlugin : Plugin<Settings> {
  override fun apply(settings: Settings) {}
}

fun Settings.includeChildrenOf(
  dir: File,
  excluding: Set<String> = emptySet(),
  toProjectName: File.() -> String = { ":${parentFile.name}-$name" },
) =
  dir.forEachChildWithABuildScript(excluding) { projectDir ->
    createProject(projectDir, projectDir.toProjectName())
  }

fun Settings.includeBuildChildrenOf(dir: File) =
  dir.forEachChildWithABuildScript { projectDir ->
    includeBuild(projectDir)
  }

fun File.forEachChildWithABuildScript(
  excluding: Set<String> = emptySet(),
  action: (File) -> Unit
) = this
  .directories()
  .filter { it.containsBuildScript() }
  .filter { !excluding.contains(it.name) }
  .sorted()
  .forEach(action)

fun File.directories() = filter { isDirectory }

fun File.files() = filter { isFile }

fun File.filter(predicate: File.() -> Boolean) = listFiles()?.filter(predicate) ?: emptyList()

fun File.containsBuildScript(): Boolean = files().any { it.isBuildScript() }

fun Settings.createProject(projectDir: File, projectName: String) {
  include(projectName)
  project(projectName).projectDir = projectDir
}

fun File.isBuildScript() = extension == "gradle" || name == "build.gradle.kts"
