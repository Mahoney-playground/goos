plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  id("org.jmailen.kotlinter") version "3.6.0"
  id("uk.org.lidalia.idea-ext")
  id("uk.org.lidalia.kotlin-flat")
  id("uk.org.lidalia.download-dependencies")
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
  }
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

