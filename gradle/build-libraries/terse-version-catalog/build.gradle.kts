plugins {
  `kotlin-dsl`
  id("org.jmailen.kotlinter") version "3.4.0"
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

group = "uk.org.lidalia"
version = "0.1.0"

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
