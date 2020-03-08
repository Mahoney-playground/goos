plugins {
  kotlin("jvm") version "1.3.70"
  `kotlin-dsl`
  id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
  `java-gradle-plugin`
}

repositories {
  jcenter()
  mavenCentral()
  gradlePluginPortal()
}

kotlinDslPluginOptions {
  experimentalWarning.set(false)
}

kotlin {
  sourceSets {
    main {
      kotlin.setSrcDirs(setOf("src"))
    }
    test {
      kotlin.setSrcDirs(setOf("tests"))
    }
  }
}

gradlePlugin {
  plugins {
    create("alt-application-plugin") {
      id = "alt-application-plugin"
      implementationClass = "uk.org.lidalia.gradle.plugins.application.ApplicationPlugin"
    }
    create("alt-build-dashboard-plugin") {
      id = "alt-build-dashboard"
      implementationClass = "uk.org.lidalia.gradle.plugins.reporting.BuildDashboardPlugin"
    }
  }
}

dependencies {
  testImplementation("org.jsoup:jsoup:1.13.1")
  testImplementation(kotest("core"))
  testImplementation(kotest("runner-junit5"))
  testImplementation("io.mockk:mockk:1.9.3")
}

tasks {
  test {
    useJUnitPlatform()
  }
}

fun kotest(module: String) = "io.kotest:kotest-$module:4.0.0-BETA1"
