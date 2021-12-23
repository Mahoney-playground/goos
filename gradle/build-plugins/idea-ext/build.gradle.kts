plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  id("org.jmailen.kotlinter") version "3.6.0"
  id("uk.org.lidalia.download-dependencies")
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
