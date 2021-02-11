@file:Suppress("UnstableApiUsage")

rootProject.name = "goos"

includeChildrenOf("appSrc") { ":${it.name}" }
includeChildrenOf("appSrc/auction")
includeChildrenOf("appSrc/ui")
includeChildrenOf("libraries") { ":${it.name}" }

include(":end-to-end-tests")

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      version("kotest", "4.3.2")
      version("arrow", "0.11.0")
      version("smack", "4.3.4")
      version("jxmpp", "0.6.4")
      version("mockk", "1.10.5")
      version("mockk", "1.10.5")
      version("kotlinxHtml", "0.7.2")
      versionCatalog(
        "marathon",
        "com.jaliansystems", { "marathon-$it" }, "5.4.0.0",
        "java-agent",
        "java-driver",
      )
      version("byteBuddy", "1.10.9")
      alias("byteBuddy")
        .to("net.bytebuddy", "byte-buddy")
        .versionRef("byteBuddy")
      versionCatalog(
        "selenium",
        "org.seleniumhq.selenium", { "selenium-$it" }, "3.141.59",
        "api",
        "remote-driver",
      )
      versionCatalog(
        "coroutines",
        "org.jetbrains.kotlinx", { "kotlinx-coroutines-$it" }, "1.4.2",
        "core",
        "core-jvm",
      )
    }
  }
}

fun VersionCatalogBuilder.versionCatalog(
  alias: String,
  group: String,
  artifactTemplate: (String) -> String,
  version: String,
  vararg modules: String,
) {
  version(alias, version)
  modules.forEach { module ->
    alias("$alias-${module.replace("-", "")}")
      .to(group, artifactTemplate(module))
      .versionRef(alias)
  }
}

fun ComponentMetadataDetails.lockVersion(group: String, name: String, version: String) {
  allVariants {
    withDependencies {
      find { it.group == group && it.name == name }?.version { require(version) }
    }
  }
}

dependencyResolutionManagement {
  @Suppress("UnstableApiUsage")
  components {
    withModule("org.igniterealtime.smack:smack-core") {
      lockVersion("org.minidns", "minidns-core", "0.3.4")
      lockVersion("org.jxmpp", "jxmpp-jid", "0.6.4")
    }
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

fun Iterable<File>.containingBuildScript() =
  filter { dir -> dir.files().any { it.isBuildScript() } }

fun createProject(file: File, projectName: String) {
  include(projectName)
  project(projectName).projectDir = file
}

fun File.isBuildScript() = extension == "gradle" || name == "build.gradle.kts"
