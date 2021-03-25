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
  gradlePluginPortal()
}

gradlePlugin {
  plugins {
    create("application-plugin") {
      id = "uk.org.lidalia.application"
      implementationClass =
        "uk.org.lidalia.gradle.plugins.application.ApplicationPlugin"
    }
  }
}

idea {
  setPackagePrefix("uk.org.lidalia.gradle.plugins.application")
}
