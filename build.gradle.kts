import com.vanniktech.dependency.graph.generator.DependencyGraphGeneratorExtension.Generator
import org.gradle.api.distribution.plugins.DistributionPlugin.TASK_INSTALL_NAME
import org.jetbrains.gradle.ext.IdeaExtPlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jmailen.gradle.kotlinter.KotlinterPlugin

plugins {
  base
  kotlin("jvm") version "1.4.30" apply false
  id("com.autonomousapps.dependency-analysis") version "0.70.0"
  id("org.jmailen.kotlinter") version "3.3.0"
  id("com.vanniktech.dependency.graph.generator") version "0.5.0"
  id("com.github.ben-manes.versions") version "0.36.0"
  id("report-aggregator")
  id("com.dorongold.task-tree") version "1.5"
}

@Suppress("UnstableApiUsage")
val javaVersion by extra(JavaLanguageVersion.of(15))

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
      @Suppress("UnstableApiUsage")
      toolchain {
        languageVersion.set(javaVersion)
        vendor.set(JvmVendorSpec.ADOPTOPENJDK)
      }

      configure<SourceSetContainer> {
        named("main") { java.setSrcDirs(mainSrc) }
        named("test") { java.setSrcDirs(testSrc) }
      }

      @Suppress("UnstableApiUsage")
      consistentResolution {
        useCompileClasspathVersions()
      }
    }

    configure<KotlinJvmProjectExtension> {
      sourceSets {
        named("main") { kotlin.setSrcDirs(mainSrc) }
        named("test") { kotlin.setSrcDirs(testSrc) }
      }
    }

    configurations.all {
      resolutionStrategy {
        @Suppress("UnstableApiUsage")
        failOnNonReproducibleResolution()
      }
    }

    @Suppress("UnstableApiUsage")
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
        }
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

    kotlinter {
      reporters = arrayOf("checkstyle", "plain", "html")
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

    dependencies {
      constraints {
        configurations.forEach { conf ->
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
