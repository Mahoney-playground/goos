plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  id("org.jmailen.kotlinter") version "3.4.0"
  id("uk.org.lidalia.idea-ext")
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
