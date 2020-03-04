plugins {
  kotlin("jvm") version "1.3.70"
  `kotlin-dsl`
  id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
}

repositories {
  jcenter()
  mavenCentral()
  gradlePluginPortal()
}

kotlinDslPluginOptions {
  experimentalWarning.set(false)
}

kotlin {
  sourceSets {
    main {
      kotlin.setSrcDirs(setOf("src"))
    }
    test {
      kotlin.setSrcDirs(setOf("tests"))
    }
  }
}
