plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  id("org.jmailen.kotlinter") version "3.2.0"
}

repositories {
  mavenCentral()
  gradlePluginPortal()
  jcenter() {
    content {
      includeModule("org.jetbrains.kotlinx", "kotlinx-html-jvm")
      includeModule("org.jetbrains.kotlinx", "kotlinx-html-common")
    }
  }
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
