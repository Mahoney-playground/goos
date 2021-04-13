import uk.org.lidalia.gradle.plugins.includebuildconventions.includeChildrenOf
import uk.org.lidalia.gradle.plugins.includebuildconventions.includeBuildChildrenOf
import uk.org.lidalia.gradle.plugins.terseversioncatalog.versionCatalog

@Suppress("UnstableApiUsage")
pluginManagement {
  includeBuild("gradle/build-plugins/include-build-conventions")
  includeBuild("gradle/build-plugins/terse-version-catalog")
}

plugins {
  id("uk.org.lidalia.include-build-conventions")
  id("uk.org.lidalia.terse-version-catalog")
}

rootProject.name = "goos"

includeBuildChildrenOf(file("gradle/build-plugins"))
includeBuild("libraries/indexhtml") {
  dependencySubstitution {
    substitute(module("uk.org.lidalia:indexhtml")).with(project(":"))
  }
}

includeChildrenOf(file("app-src")) { ":$name" }
includeChildrenOf(file("app-src/auction"))
includeChildrenOf(file("app-src/ui"))
includeChildrenOf(file("libraries"), excluding = setOf("indexhtml")) { ":$name" }

include(":end-to-end-tests")

enableFeaturePreview("VERSION_CATALOGS")

val jxmppVersion = "0.6.4"

dependencyResolutionManagement {
  @Suppress("UnstableApiUsage")
  versionCatalogs {
    create("libs") {
      versionCatalog(
        "kotest",
        "io.kotest", { "kotest-$it" }, "4.4.3",
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
