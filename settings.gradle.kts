rootProject.name = "goos"

include(
    ":core",
    ":end-to-end-tests"
)

includeChildrenOf("libraries") { ":${it.name}" }

buildCache {
  local {
    directory = "${settingsDir}/caches/build-cache"
  }
}

fun includeChildrenOf(
    container: String,
    nameFunc: (File) -> String = { ":${it.parentFile.name}-${it.name}" }
) = file(container)
    .directories()
    .containingBuildScript()
    .forEach {
        createProject(it, nameFunc(it))
    }

fun File.directories() = listFiles()
    ?.filter { it.isDirectory }
    ?.toList() ?: emptyList()

fun File.files() = listFiles()
    ?.filter { it.isFile }
    ?.toList() ?: emptyList()

fun Iterable<File>.containingBuildScript() = this.filter { dir ->
    dir.files().any { it.isBuildScript() }
}

fun createProject(file: File, projectName: String) {
    include(projectName)
    project(projectName).projectDir = file
}

fun File.isBuildScript() = extension == "gradle" || name == "build.gradle.kts"
