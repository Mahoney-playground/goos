plugins {
  kotlin("jvm") version "1.5.21"
  id("org.jmailen.kotlinter") version "3.6.0"
  id("uk.org.lidalia.ideaext") version "0.2.0"
  id("uk.org.lidalia.kotlin-flat")
  id("uk.org.lidalia.downloaddependencies") version "0.4.0"
}

repositories {
  mavenCentral()
}

group = "uk.org.lidalia"
version = "0.1.0"

configure<JavaPluginExtension> {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
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

dependencies {
  @Suppress("GradlePackageUpdate")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
  implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.8.0")
}

idea {
  setPackagePrefix("uk.org.lidalia.indexhtml")
}
