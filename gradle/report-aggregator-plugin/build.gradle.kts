@file:Suppress("UnstableApiUsage")

plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  id("org.jmailen.kotlinter") version "3.2.0"
  id("uk.org.lidalia.idea-ext-plugin")
  id("uk.org.lidalia.kotlin-flat-plugin")
}

repositories {
  mavenCentral()
  jcenter()
  gradlePluginPortal()
}

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
  implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
}

gradlePlugin {
  plugins {
    create("report-aggregator-plugin") {
      id = "uk.org.lidalia.report-aggregator"
      implementationClass =
        "uk.org.lidalia.gradle.plugins.reportaggregator.ReportAggregatorPlugin"
    }
  }
}

idea {
  setPackagePrefix("uk.org.lidalia.gradle.plugins.reportaggregator")
}
