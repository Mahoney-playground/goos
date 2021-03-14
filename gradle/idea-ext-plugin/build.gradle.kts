@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

val javaVersion by extra(JavaLanguageVersion.of(15))

java {
  toolchain {
    languageVersion.set(javaVersion)
    vendor.set(JvmVendorSpec.ADOPTOPENJDK)
  }
}

tasks {

  withType<KotlinCompile> {
    kotlinOptions.apply {
      jvmTarget = javaVersion.toString()
      useIR = true
    }
  }
}

dependencies {
  implementation("gradle.plugin.org.jetbrains.gradle.plugin.idea-ext:gradle-idea-ext:0.9")
}

gradlePlugin {
  plugins {
    create("idea-ext-plugin") {
      id = "lidalia-idea-plugin"
      implementationClass =
        "uk.org.lidalia.gradle.plugins.idea.IdeaPlugin"
    }
  }
}
