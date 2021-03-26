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

gradlePlugin {
  plugins {
    create("copy-without-version") {
      id = "uk.org.lidalia.copy-without-version"
      implementationClass =
        "uk.org.lidalia.gradle.plugins.copywithoutversion.CopyWithoutVersionPlugin"
    }
  }
}

idea {
  setPackagePrefix("uk.org.lidalia.gradle.plugins.copywithoutversion")
}

