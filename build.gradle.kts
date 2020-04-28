import com.vanniktech.dependency.graph.generator.DependencyGraphGeneratorExtension.Generator
import org.gradle.api.JavaVersion.VERSION_14
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintPlugin

plugins {
  base
  kotlin("jvm") version kotlinVersion apply false
  id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
  id("com.vanniktech.dependency.graph.generator") version "0.5.0"
  id("com.github.ben-manes.versions") version "0.27.0"
  id("report-aggregator")
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

  apply<ProjectReportsPlugin>()

  afterEvaluate {
    tasks.check {
      dependsOn(tasks.getByName("projectReport"))
    }
  }
}

subprojects {

  pluginManager.withPlugin("kotlin") {

    apply<KtlintPlugin>()
    apply<BuildDashboardPlugin>()

    val test by tasks.existing(Test::class)

    val api by configurations
    val testImplementation by configurations

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
      api(kotlin("stdlib"))

      testImplementation(kotest("core"))
      testImplementation(kotest("runner-junit5"))
      testImplementation("io.mockk:mockk:1.9.3")
    }

    tasks.withType<KotlinCompile> {
      kotlinOptions.jvmTarget = "13" // hardcoded until kotlin can cope with 14
    }

    tasks {
      test {
        useJUnitPlatform()
      }
    }

    tasks.register<DownloadDependenciesTask>("downloadDependencies")
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
