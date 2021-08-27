plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  id("org.jmailen.kotlinter") version "3.5.0"
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

tasks.register("downloadDependencies") {
  // dummy to allow the build to pass!
}
