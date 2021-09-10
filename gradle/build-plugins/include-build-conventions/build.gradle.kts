plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  id("org.jmailen.kotlinter") version "3.6.0"
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

gradlePlugin {
  plugins {
    create("include-build-conventions") {
      id = "uk.org.lidalia.include-build-conventions"
      implementationClass =
        "uk.org.lidalia.gradle.plugins.includebuildconventions.IncludeBuildConventionsPlugin"
    }
  }
}

tasks.register("downloadDependencies") {
  // dummy to allow the build to pass!
}
