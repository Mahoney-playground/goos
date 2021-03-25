@file:Suppress("UnstableApiUsage")

rootProject.name = "goos"

includeBuildChildrenOf("gradle/build-plugins")
includeBuild("gradle/shared-libraries/indexhtml") {
  dependencySubstitution {
    substitute(module("uk.org.lidalia:indexhtml")).with(project(":"))
  }
}

includeChildrenOf("app-src") { ":$name" }
includeChildrenOf("app-src/auction")
includeChildrenOf("app-src/ui")
includeChildrenOf("libraries") { ":$name" }

include(":end-to-end-tests")

enableFeaturePreview("VERSION_CATALOGS")

apply(from = "gradle/versions.gradle.kts")

fun includeChildrenOf(
  dir: String,
  toProjectName: File.() -> String = { ":${parentFile.name}-$name" }
) =
  dir.forEachChildWithABuildScript { projectDir ->
    createProject(projectDir, projectDir.toProjectName())
  }

fun includeBuildChildrenOf(dir: String) =
  dir.forEachChildWithABuildScript { projectDir ->
    includeBuild(projectDir)
  }

fun String.forEachChildWithABuildScript(action: (File) -> Unit) = file(this)
  .directories()
  .filter { it.containsBuildScript() }
  .forEach(action)

fun File.directories() = filter { isDirectory }

fun File.files() = filter { isFile }

fun File.filter(predicate: File.() -> Boolean) = listFiles()?.filter(predicate) ?: emptyList()

fun File.containsBuildScript(): Boolean = files().any { it.isBuildScript() }

fun createProject(projectDir: File, projectName: String) {
  include(projectName)
  project(projectName).projectDir = projectDir
}

fun File.isBuildScript() = extension == "gradle" || name == "build.gradle.kts"
