import uk.org.lidalia.gradle.plugins.includebuildconventions.includeChildrenOf
import uk.org.lidalia.gradle.plugins.includebuildconventions.includeBuildChildrenOf

@Suppress("UnstableApiUsage")
pluginManagement {
  includeBuild("gradle/build-plugins/include-build-conventions")
}

plugins {
  id("uk.org.lidalia.include-build-conventions")
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

apply(from = "gradle/versions.gradle.kts")
