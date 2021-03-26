plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  id("org.jmailen.kotlinter") version "3.2.0"
  id("uk.org.lidalia.idea-ext")
  id("uk.org.lidalia.kotlin-flat")
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation("uk.org.lidalia:indexhtml:0.1.0")
}

gradlePlugin {
  plugins {
    create("report-aggregator") {
      id = "uk.org.lidalia.report-aggregator"
      implementationClass =
        "uk.org.lidalia.gradle.plugins.reportaggregator.ReportAggregatorPlugin"
    }
  }
}

idea {
  setPackagePrefix("uk.org.lidalia.gradle.plugins.reportaggregator")
}