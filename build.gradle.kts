@file:Suppress("UnstableApiUsage")

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.gundy.semver4j.model.Version
import com.vanniktech.dependency.graph.generator.DependencyGraphGeneratorExtension.Generator
import org.gradle.api.distribution.plugins.DistributionPlugin.TASK_INSTALL_NAME
import org.gradle.internal.deprecation.DeprecatableConfiguration
import org.jmailen.gradle.kotlinter.KotlinterPlugin
import uk.org.lidalia.gradle.plugins.copywithoutversion.CopyWithoutVersionsTask
import uk.org.lidalia.gradle.plugin.downloaddependencies.LidaliaDownloadDependenciesPlugin
import uk.org.lidalia.gradle.plugin.ideaext.LidaliaIdeaExtPlugin
import uk.org.lidalia.gradle.plugins.kotlinflat.KotlinFlatPlugin
import java.lang.IllegalArgumentException

buildscript {
  dependencies {
    classpath("com.github.gundy:semver4j:0.16.4")
  }
}

plugins {
  base
  kotlin("jvm") version "1.8.0" apply false
  id("uk.org.lidalia.kotlin-flat") apply false
  id("uk.org.lidalia.ideaext") version "0.2.0" apply false
  id("uk.org.lidalia.downloaddependencies") version "0.4.0"
  id("uk.org.lidalia.copy-without-version")
  id("com.autonomousapps.dependency-analysis") version "1.18.0"
  id("org.jmailen.kotlinter") version "3.13.0"
  id("com.vanniktech.dependency.graph.generator") version "0.8.0"
  id("com.github.ben-manes.versions") version "0.44.0"
  id("com.dorongold.task-tree") version "2.1.0"
}

apply<ReportingBasePlugin>()

allprojects {
  group = "uk.org.lidalia.goos"
  version = "0.1.0"

  repositories {
    mavenCentral()
  }
  apply<LidaliaDownloadDependenciesPlugin>()
  apply<KotlinterPlugin>()

  tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
      candidate.version.isPreRelease()
    }
  }
}

val rootBuildDir = buildDir

subprojects {

  val relativeProjectPath = rootProject.projectDir.toPath().relativize(this.projectDir.toPath())
  buildDir = rootProject.file("${rootBuildDir}/$relativeProjectPath")

  pluginManager.withPlugin("kotlin") {

    apply<KotlinFlatPlugin>()
    apply<BuildDashboardPlugin>()
    apply<LidaliaIdeaExtPlugin>()

    configure<JavaPluginExtension> {
      toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
      }

      consistentResolution {
        useCompileClasspathVersions()
      }
    }

//    configurations.all {
//      resolutionStrategy {
//        failOnNonReproducibleResolution()
//      }
//    }

    dependencies {

      val testImplementation by configurations

      testImplementation(libs.kotest.runnerJunit5)
    }

    tasks {

      withType<Jar>().configureEach {
        archiveVersion.convention(null as String?)
      }

      named<Test>("test") {
        environment("BUILD_SYSTEM", "GRADLE")
        useJUnitPlatform()
        jvmArgs(
          "-Xshare:off",
        )
      }

      register<DependencyReportTask>("allDeps")
    }

    kotlinter {
      reporters = arrayOf("checkstyle", "plain", "html")
    }
  }

  pluginManager.withPlugin("java-test-fixtures") {
    dependencies {
      constraints {
        configurations
          .filter { it !is DeprecatableConfiguration || it.declarationAlternatives == null }
          .forEach { conf ->
            add(conf.name, "org.minidns:minidns-core") {
              version {
                strictly("1.0.2")
                because("minidns-core is declared by smack-core as a dependency range but we fail on non reproducible resolution")
              }
            }
            add(conf.name, libs.jxmpp.jid)
          }
      }
    }
  }
}

dependencyGraphGenerator {
  generators {
    add(
      Generator.ALL.copy(
        includeProject = { it.pluginManager.hasPlugin("java") }
      )
    )
    add(
      Generator(
        name = "main",
        include = {
          !it.moduleGroup.startsWith("org.jetbrains")
        },
        includeProject = { it.pluginManager.hasPlugin("java") }
      )
    )
  }
}

val appJavaAgents: Configuration by configurations.creating

dependencies {
  appJavaAgents(
    project(
      mapOf(
        "path" to ":app",
        "configuration" to "javaAgents"
      )
    )
  )
}

tasks {

  val app = project(":app")
  val copyJavaAgents by registering(CopyWithoutVersionsTask::class) {
    from = appJavaAgents
    into = buildDir.resolve(project.name).resolve("lib/agents")
    dependsOn(app.tasks.assemble)
  }

  assemble {

    dependsOn(app.tasks.assemble)
    dependsOn(copyJavaAgents)
    doLast {
      copy {
        from(app.tasks.getByName(TASK_INSTALL_NAME))
        into(buildDir.resolve(project.name))
      }
    }
  }

  check {
    dependsOn("buildHealth")
    dependsOn("installKotlinterPrePushHook")
  }

  listOf(
    clean,
    check,
    register("lintKotlin"),
    register("formatKotlin"),
  ).forEach { task ->
    task {
      dependsOn(gradle.includedBuilds.map { it.task(":${task.name}") })
    }
  }
}

dependencyAnalysis {
  issues {
    // configure for all projects
    all {
      // set behavior for all issue types
      onAny {
        severity("fail")
      }
    }
  }
}

fun String.isPreRelease(): Boolean = try {
  Version.fromString(this).preReleaseIdentifiers.isNotEmpty()
} catch (e: IllegalArgumentException) {
  false
}
