@file:Suppress("unused", "UnstableApiUsage")

package uk.org.lidalia.gradle.plugins.terseversioncatalog

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.dsl.VersionCatalogBuilder

class TerseVersionCatalogPlugin : Plugin<Settings> {
  override fun apply(settings: Settings) {}
}

fun VersionCatalogBuilder.versionCatalog(
  alias: String,
  group: String,
  artifact: String,
  version: String,
) {
  version(alias, version)
  library(alias, group, artifact).versionRef(alias)
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
    library("$alias-$sanitisedModuleName", group, artifactTemplate(module)).versionRef(alias)
  }
}

val hyphenThenLetter = "-([a-zA-Z])".toRegex()

fun String.hyphenToCamelCase(): String = replace(hyphenThenLetter) { matchResult ->
  matchResult.groupValues[1].toUpperCase()
}
