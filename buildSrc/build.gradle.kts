plugins {
  kotlin("jvm") version "1.3.70"
  `kotlin-dsl`
  id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
  `java-gradle-plugin`
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

gradlePlugin {
  plugins {
    create("alt-application-plugin") {
      id = "alt-application-plugin"
      implementationClass = "uk.org.lidalia.gradle.plugins.application.ApplicationPlugin"
    }
    create("alt-build-dashboard-plugin") {
      id = "alt-build-dashboard"
      implementationClass = "uk.org.lidalia.gradle.plugins.reporting.BuildDashboardPlugin"
    }
  }
}

dependencies {
  testRuntimeOnly("org.javamodularity:moduleplugin:1.5.0")
}
