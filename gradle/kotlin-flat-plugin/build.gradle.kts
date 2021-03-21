@file:Suppress("UnstableApiUsage")

plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  id("org.jmailen.kotlinter") version "3.2.0"
  id("uk.org.lidalia.idea-ext-plugin")
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
  api("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.31")
}

gradlePlugin {
  plugins {
    create("kotlin-flat-plugin") {
      id = "uk.org.lidalia.kotlin-flat-plugin"
      implementationClass =
        "uk.org.lidalia.gradle.plugins.kotlinflat.KotlinFlatPlugin"
    }
  }
}

idea {
  setPackagePrefix("uk.org.lidalia.gradle.plugins.kotlinflat")
}
