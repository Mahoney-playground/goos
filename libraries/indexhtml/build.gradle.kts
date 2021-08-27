import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.5.30"
  id("org.jmailen.kotlinter") version "3.5.0"
  id("uk.org.lidalia.idea-ext")
  id("uk.org.lidalia.kotlin-flat")
  id("uk.org.lidalia.download-dependencies")
}

repositories {
  mavenCentral()
}

group = "uk.org.lidalia"
version = "0.1.0"


val javaVersion by extra(JavaLanguageVersion.of(15))

configure<JavaPluginExtension> {
  toolchain {
    languageVersion.set(javaVersion)
    vendor.set(JvmVendorSpec.ADOPTOPENJDK)
  }

  @Suppress("UnstableApiUsage")
  consistentResolution {
    useCompileClasspathVersions()
  }
}

configurations.all {
  resolutionStrategy {
    failOnNonReproducibleResolution()
  }
}

tasks {
  withType<KotlinCompile> {
    kotlinOptions.apply {
      jvmTarget = javaVersion.toString()
      freeCompilerArgs = listOf("-Xinline-classes")
    }
  }
}

dependencies {
  @Suppress("GradlePackageUpdate")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
  implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.3")
}

idea {
  setPackagePrefix("uk.org.lidalia.indexhtml")
}
