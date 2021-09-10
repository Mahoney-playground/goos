plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  id("org.jmailen.kotlinter") version "3.5.0"
}

repositories {
  mavenCentral()
  gradlePluginPortal()
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
