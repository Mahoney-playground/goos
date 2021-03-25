val jxmppVersion = "0.6.4"

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      versionCatalog(
        "kotest",
        "io.kotest", { "kotest-$it" }, "4.4.1",
        "framework-api",
        "framework-api-jvm",
        "assertions-core",
        "assertions-core-jvm",
        "assertions-shared",
        "assertions-shared-jvm",
        "runner-junit5",
        "extensions-testcontainers",
      )
      versionCatalog(
        "arrow",
        "io.arrow-kt", { "arrow-$it" }, "0.11.0",
        "core-data",
      )
      versionCatalog(
        "smack",
        "org.igniterealtime.smack", { "smack-$it" }, "4.3.4",
        "core",
        "tcp",
        "im",
        "extensions",
        "java7",
      )
      versionCatalog(
        "jxmpp",
        "org.jxmpp", { "jxmpp-$it" }, jxmppVersion,
        "jid",
      )
      versionCatalog(
        "mockk",
        "io.mockk", { if (it == null) "mockk" else "mockk-$it" }, "1.10.5",
        null,
        "dsl-jvm",
      )
      versionCatalog(
        "kotlinxHtml",
        "org.jetbrains.kotlinx", { "kotlinx-html-$it" }, "0.7.3",
        "jvm",
      )
      versionCatalog(
        "marathon",
        "com.jaliansystems", { "marathon-$it" }, "5.4.0.0",
        "java-agent",
        "java-driver",
      )
      versionCatalog(
        "byteBuddy",
        "net.bytebuddy", "byte-buddy", "1.10.9"
      )
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
  artifact: String,
  version: String,
) {
  version(alias, version)
  alias(alias).to(group, artifact).versionRef(alias)
}

fun VersionCatalogBuilder.versionCatalog(
  alias: String,
  group: String,
  artifactTemplate: (String?) -> String,
  version: String,
  vararg modules: String?,
) {
  modules.forEach { module ->
    version(alias, version)
    val sanitisedModuleName = module?.hyphenToCamelCase() ?: "core"
    alias("$alias-$sanitisedModuleName")
      .to(group, artifactTemplate(module))
      .versionRef(alias)
  }
}

fun String.hyphenToCamelCase(): String {
  return this.replace("-([a-zA-Z])".toRegex()) { matchResult -> matchResult.groupValues[1].toUpperCase() }
}
