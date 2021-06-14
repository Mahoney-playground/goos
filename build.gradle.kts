@file:Suppress("UnstableApiUsage")

import com.vanniktech.dependency.graph.generator.DependencyGraphGeneratorExtension.Generator
import org.gradle.api.distribution.plugins.DistributionPlugin.TASK_INSTALL_NAME
import org.gradle.internal.deprecation.DeprecatableConfiguration
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jmailen.gradle.kotlinter.KotlinterPlugin
import uk.org.lidalia.gradle.plugins.copywithoutversion.CopyWithoutVersionsTask
import uk.org.lidalia.gradle.plugins.downloaddeps.DownloadDependenciesPlugin
import uk.org.lidalia.gradle.plugins.idea.IdeaPlugin
import uk.org.lidalia.gradle.plugins.kotlinflat.KotlinFlatPlugin

plugins {
  base
  kotlin("jvm") version "1.5.10" apply false
  id("uk.org.lidalia.kotlin-flat") apply false
  id("uk.org.lidalia.idea-ext") apply false
  id("uk.org.lidalia.download-dependencies")
  id("uk.org.lidalia.copy-without-version")
  id("com.autonomousapps.dependency-analysis") version "0.73.0"
  id("org.jmailen.kotlinter") version "3.4.5"
  id("com.vanniktech.dependency.graph.generator") version "0.5.0"
  id("com.github.ben-manes.versions") version "0.38.0"
  id("uk.org.lidalia.report-aggregator")
  id("com.dorongold.task-tree") version "1.5"
}

val javaVersion by extra(JavaLanguageVersion.of(15))

apply<ReportingBasePlugin>()

allprojects {
  group = "uk.org.lidalia.goos"
  version = "0.1.0"

  repositories {
    mavenCentral()
  }
  apply<DownloadDependenciesPlugin>()
  apply<KotlinterPlugin>()
}

subprojects {

  pluginManager.withPlugin("kotlin") {

    apply<KotlinFlatPlugin>()
    apply<BuildDashboardPlugin>()
    apply<IdeaPlugin>()

    configure<JavaPluginExtension> {
      toolchain {
        languageVersion.set(javaVersion)
        vendor.set(JvmVendorSpec.ADOPTOPENJDK)
      }

      consistentResolution {
        useCompileClasspathVersions()
      }
    }

    configurations.all {
      resolutionStrategy {
        failOnNonReproducibleResolution()
      }
    }

    dependencies {

      val testImplementation by configurations

      testImplementation(libs.kotest.runnerJunit5)
      testImplementation(libs.mockk.core)
    }

    tasks {

      withType<KotlinCompile> {
        kotlinOptions.apply {
          jvmTarget = javaVersion.toString()
          useIR = true
          freeCompilerArgs = listOf("-Xinline-classes")
        }
      }

      withType<Jar> {
        archiveVersion.convention(null as String?)
      }

      named<Test>("test") {
        environment("BUILD_SYSTEM", "GRADLE")
        useJUnitPlatform()
        jvmArgs(
          "-Xshare:off",
          "--illegal-access=deny"
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
                strictly("0.3.4")
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
  generators = listOf(
    Generator.ALL.copy(
      includeProject = { it.pluginManager.hasPlugin("java") }
    ),
    Generator(
      name = "main",
      include = {
        !it.moduleGroup.startsWith("org.jetbrains")
      },
      includeProject = { it.pluginManager.hasPlugin("java") }
    )
  )
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
