@file:Suppress("UnstableApiUsage")

rootProject.name = "goos"

includeChildrenOf("app-src") { ":${it.name}" }
includeChildrenOf("app-src/auction")
includeChildrenOf("app-src/ui")
includeChildrenOf("libraries") { ":${it.name}" }

include(":end-to-end-tests")

apply(from = "gradle/versions.gradle.kts")

fun includeChildrenOf(
  container: String,
  nameFunc: (File) -> String = { ":${it.parentFile.name}-${it.name}" }
) = file(container)
  .directories()
  .containingBuildScript()
  .forEach {
    createProject(it, nameFunc(it))
  }

fun File.directories() = filter { isDirectory }

fun File.files() = filter { isFile }

fun File.filter(predicate: File.() -> Boolean) = listFiles()
  ?.filter(predicate)
  ?.toList() ?: emptyList()

fun Iterable<File>.containingBuildScript() =
  filter { dir -> dir.files().any { it.isBuildScript() } }

fun createProject(file: File, projectName: String) {
  include(projectName)
  project(projectName).projectDir = file
}

fun File.isBuildScript() = extension == "gradle" || name == "build.gradle.kts"
