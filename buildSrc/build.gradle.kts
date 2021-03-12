plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  id("org.jmailen.kotlinter") version "3.2.0"
}

repositories {
  jcenter()
  mavenCentral()
  gradlePluginPortal()
}

kotlin {
  sourceSets {
    main {
      kotlin.setSrcDirs(setOf("src"))
    }
    test {
      kotlin.setSrcDirs(setOf("test"))
    }
  }
}

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
  implementation("gradle.plugin.org.jetbrains.gradle.plugin.idea-ext:gradle-idea-ext:0.9")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
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
