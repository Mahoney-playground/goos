 import org.gradle.plugins.ide.idea.model.IdeaModel
 import org.jetbrains.gradle.ext.ModuleSettings
 import org.jetbrains.gradle.ext.PackagePrefixContainer

plugins {
  kotlin("jvm")
  id("alt-application-plugin")
  id("org.jetbrains.gradle.plugin.idea-ext") version "0.7" apply true
}

dependencies {
  implementation(smack("core"))
  implementation(smack("tcp"))
  implementation(smack("extensions"))
  implementation(smack("java7"))
  implementation(smack("im"))
  implementation(project(":kotlinlangext"))
  testImplementation(marathon("java-driver"))
  testImplementation(project(":ui-test-support"))
}

application {
  mainClassName = "goos.core.app.Main"
}

idea {
  setPackagePrefix("goos.core")
}

val IdeaModel.packagePrefix: PackagePrefixContainer
  get() {
    val settings: ModuleSettings by (module as ExtensionAware).extensions
    val packagePrefix: PackagePrefixContainer by (settings as ExtensionAware).extensions
    return packagePrefix
  }

fun IdeaModel.setPackagePrefix(prefix: String) {

  val gradleProject = module?.project ?: project?.project

  gradleProject!!.sourceSets
    .flatMap { it.java.srcDirs }
    .map { it.relativeTo(gradleProject.projectDir).path }
    .forEach {
      packagePrefix[it] = prefix
    }
}
