plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  id("org.jmailen.kotlinter") version "3.6.0"
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
    vendor.set(JvmVendorSpec.matching("Temurin"))
  }
}

gradlePlugin {
  plugins {
    create("download-dependencies") {
      id = "uk.org.lidalia.download-dependencies"
      implementationClass =
        "uk.org.lidalia.gradle.plugins.downloaddeps.DownloadDependenciesPlugin"
    }
  }
}

tasks.register("downloadDependencies") {
  // dummy to allow the build to pass!
}
