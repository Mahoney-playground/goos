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
    substitute(module("uk.org.lidalia:indexhtml")).using(project(":"))
  }
}

includeChildrenOf(file("app-src")) { ":$name" }
includeChildrenOf(file("app-src/auction"))
includeChildrenOf(file("app-src/ui"))
includeChildrenOf(file("libraries"), excluding = setOf("indexhtml")) { ":$name" }

include(":end-to-end-tests")

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

val jxmppVersion = "1.0.3"

dependencyResolutionManagement {
  @Suppress("UnstableApiUsage")
  versionCatalogs {
    create("libs") {
      versionCatalog(
        "kotest",
        "io.kotest", { "kotest-$it" }, "5.1.0",
        "common-jvm",
        "framework-api",
        "framework-api-jvm",
        "assertions-core",
        "assertions-core-jvm",
        "assertions-shared",
        "assertions-shared-jvm",
        "runner-junit5",
      )
      versionCatalog(
        "kotestextensions-testcontainers",
        "io.kotest.extensions", "kotest-extensions-testcontainers", "1.1.1",
      )
      versionCatalog(
        "testcontainers",
        "org.testcontainers",
        "testcontainers",
        "1.16.2",
      )
      versionCatalog(
        "slf4j",
        "org.slf4j", { "slf4j-$it" }, "1.7.32",
        "api"
      )
      versionCatalog(
        "arrow",
        "io.arrow-kt", { "arrow-$it" }, "0.12.1",
        "core-data",
      )
      versionCatalog(
        "smack",
        "org.igniterealtime.smack", { "smack-$it" }, "4.4.4",
        "core",
        "tcp",
        "im",
        "extensions",
        "java8",
      )
      versionCatalog(
        "jxmpp",
        "org.jxmpp", { "jxmpp-$it" }, jxmppVersion,
        "jid",
      )
      versionCatalog(
        "mockk",
        "io.mockk", { if (it == null) "mockk" else "mockk-$it" }, "1.12.1",
        null,
        "dsl-jvm",
        "dsl",
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
        "org.seleniumhq.selenium", { "selenium-$it" }, "4.1.1",
        "api",
        "remote-driver",
      )
      versionCatalog(
        "coroutines",
        "org.jetbrains.kotlinx", { "kotlinx-coroutines-$it" }, "1.5.2",
        "core",
        "core-jvm",
      )
      versionCatalog(
        "dockerJava",
        "com.github.docker-java", { "docker-java-$it" }, "3.2.12",
        "core",
      )
      versionCatalog(
        "junitPlatform",
        "org.junit.platform", { "junit-platform-$it" }, "1.8.2",
        "console",
      )
      versionCatalog(
        "indexhtml",
        "uk.org.lidalia", "indexhtml", "0.1.0",
      )
      versionCatalog(
        "logback",
        "ch.qos.logback", { "logback-$it" }, "1.2.9",
        "classic",
      )
    }
  }
}
