plugins {
  kotlin("jvm") version "1.3.72"
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

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.1")
  implementation("gradle.plugin.org.jetbrains.gradle.plugin.idea-ext:gradle-idea-ext:0.7")
}

gradlePlugin {
  plugins {
    create("alt-application-plugin") {
      id = "alt-application-plugin"
      implementationClass =
        "uk.org.lidalia.gradle.plugins.application.ApplicationPlugin"
    }
    create("report-aggregator-plugin") {
      id = "report-aggregator"
      implementationClass =
        "uk.org.lidalia.gradle.plugins.reportaggregator.ReportAggregatorPlugin"
    }
  }
}
