rootProject.name = "goos"

includeBuildChildrenOf("gradle/build-plugins")
includeBuild("libraries/indexhtml") {
  dependencySubstitution {
    substitute(module("uk.org.lidalia:indexhtml")).with(project(":"))
  }
}

includeChildrenOf("app-src") { ":$name" }
includeChildrenOf("app-src/auction")
includeChildrenOf("app-src/ui")
includeChildrenOf("libraries", excluding = setOf("indexhtml")) { ":$name" }

include(":end-to-end-tests")

enableFeaturePreview("VERSION_CATALOGS")

apply(from = "gradle/versions.gradle.kts")

fun includeChildrenOf(
  dir: String,
  excluding: Set<String> = emptySet(),
  toProjectName: File.() -> String = { ":${parentFile.name}-$name" },
) =
  dir.forEachChildWithABuildScript(excluding) { projectDir ->
    createProject(projectDir, projectDir.toProjectName())
  }

fun includeBuildChildrenOf(dir: String) =
  dir.forEachChildWithABuildScript { projectDir ->
    includeBuild(projectDir)
  }

fun String.forEachChildWithABuildScript(
  excluding: Set<String> = emptySet(),
  action: (File) -> Unit
) = file(this)
  .directories()
  .filter { it.containsBuildScript() }
  .filter { !excluding.contains(it.name) }
  .sorted()
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
