plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  id("org.jmailen.kotlinter") version "3.6.0"
  id("uk.org.lidalia.ideaext") version "0.2.0"
  id("uk.org.lidalia.kotlin-flat")
  id("uk.org.lidalia.downloaddependencies") version "0.4.0"
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
  }
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
