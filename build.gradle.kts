import com.vanniktech.dependency.graph.generator.DependencyGraphGeneratorExtension.Generator
import org.gradle.api.JavaVersion.VERSION_14
import org.gradle.api.distribution.plugins.DistributionPlugin.TASK_INSTALL_NAME
import org.jetbrains.gradle.ext.IdeaExtPlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jmailen.gradle.kotlinter.KotlinterPlugin

plugins {
  base
  kotlin("jvm") version "1.4.10" apply false
  id("com.autonomousapps.dependency-analysis") version "0.56.0"
  id("org.jmailen.kotlinter") version "3.2.0"
  id("com.vanniktech.dependency.graph.generator") version "0.5.0"
  id("com.github.ben-manes.versions") version "0.27.0"
  id("report-aggregator")
  id("com.dorongold.task-tree") version "1.5"
}

@Suppress("UnstableApiUsage")
val javaVersion by extra(VERSION_14)

apply<ReportingBasePlugin>()

allprojects {
  group = "uk.org.lidalia.goos"
  version = "0.1.0"

  repositories {
    jcenter()
    mavenCentral()
  }

  tasks.register<DownloadDependenciesTask>("downloadDependencies")
}

subprojects {

  pluginManager.withPlugin("kotlin") {

    apply<KotlinterPlugin>()
    apply<BuildDashboardPlugin>()
    apply<IdeaExtPlugin>()

    val mainSrc = setOf("src")
    val testSrc = setOf("tests")

    configure<JavaPluginExtension> {
      sourceCompatibility = javaVersion
      targetCompatibility = javaVersion

      configure<SourceSetContainer> {
        named("main") { java.setSrcDirs(mainSrc) }
        named("test") { java.setSrcDirs(testSrc) }
      }
    }

    configure<KotlinJvmProjectExtension> {
      sourceSets {
        named("main") { kotlin.setSrcDirs(mainSrc) }
        named("test") { kotlin.setSrcDirs(testSrc) }
      }
    }

    dependencies {

      val testImplementation by configurations

      testImplementation(kotest("runner-junit5"))
      testImplementation(mockk)
    }

    tasks {

      withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "13" // hardcoded until kotlin can cope with 14
      }

      withType<Jar> {
        @Suppress("UnstableApiUsage")
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
  }

  pluginManager.withPlugin("java-test-fixtures") {
    val testFixturesSrc = setOf("testFixtures")

    configure<JavaPluginExtension> {
      configure<SourceSetContainer> {
        named("testFixtures") { java.setSrcDirs(testFixturesSrc) }
      }
    }
    configure<KotlinJvmProjectExtension> {
      sourceSets {
        named("testFixtures") { kotlin.setSrcDirs(testFixturesSrc) }
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

  named("build") {
    val app = project(":app")
    val appInstallTask = app.tasks.getByName(TASK_INSTALL_NAME)

    dependsOn(appInstallTask)
    dependsOn(named("buildHealth"))
    doLast {
      copy {
        from(appInstallTask)
        into(buildDir.resolve(project.name))
      }
    }
    doLast {
      copy {
        from(appJavaAgents)
        into(buildDir.resolve(project.name).resolve("lib/agents"))
      }
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
