@file:Suppress("UnstableApiUsage")

rootProject.name = "goos"

includeChildrenOf("app-src") { ":$name" }
includeChildrenOf("app-src/auction")
includeChildrenOf("app-src/ui")
includeChildrenOf("libraries") { ":$name" }

include(":end-to-end-tests")

apply(from = "gradle/versions.gradle.kts")

fun includeChildrenOf(
  dir: String,
  toProjectName: File.() -> String = { ":${parentFile.name}-$name" }
) = file(dir)
  .directories()
  .filter { it.containsBuildScript() }
  .forEach { projectDir ->
    createProject(projectDir, projectDir.toProjectName())
  }

fun File.directories() = filter { isDirectory }

fun File.files() = filter { isFile }

fun File.filter(predicate: File.() -> Boolean) = listFiles()?.filter(predicate) ?: emptyList()

fun File.containsBuildScript(): Boolean = files().any { it.isBuildScript() }

fun createProject(projectDir: File, projectName: String) {
  include(projectName)
  project(projectName).projectDir = projectDir
}

fun File.isBuildScript() = extension == "gradle" || name == "build.gradle.kts"
