@file:Suppress("UnstableApiUsage")

plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  id("org.jmailen.kotlinter") version "3.2.0"
}

repositories {
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
  api("gradle.plugin.org.jetbrains.gradle.plugin.idea-ext:gradle-idea-ext:1.0")
}

gradlePlugin {
  plugins {
    create("idea-ext") {
      id = "uk.org.lidalia.idea-ext"
      implementationClass =
        "uk.org.lidalia.gradle.plugins.idea.IdeaPlugin"
    }
  }
}