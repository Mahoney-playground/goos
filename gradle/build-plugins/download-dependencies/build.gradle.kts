plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  id("org.jmailen.kotlinter") version "3.5.0"
  id("uk.org.lidalia.idea-ext")
  id("uk.org.lidalia.kotlin-flat")
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

idea {
  setPackagePrefix("uk.org.lidalia.gradle.plugins.downloaddeps")
}

tasks.register("downloadDependencies") {
  // dummy to allow the build to pass!
}
