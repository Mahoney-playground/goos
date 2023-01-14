plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  id("org.jmailen.kotlinter") version "3.6.0"
  id("uk.org.lidalia.downloaddependencies") version "0.3.0"
  id("uk.org.lidalia.ideaext") version "0.1.0"
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
  api("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
}

gradlePlugin {
  plugins {
    create("kotlin-flat") {
      id = "uk.org.lidalia.kotlin-flat"
      implementationClass =
        "uk.org.lidalia.gradle.plugins.kotlinflat.KotlinFlatPlugin"
    }
  }
}

idea {
  setPackagePrefix("uk.org.lidalia.gradle.plugins.kotlinflat")
}
