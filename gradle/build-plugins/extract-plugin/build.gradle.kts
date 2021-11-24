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

dependencies {
  implementation("uk.org.lidalia:indexhtml:0.1.0")
  implementation("org.eclipse.jgit:org.eclipse.jgit:5.13.0.202109080827-r")
//  implementation("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")
  testImplementation("io.kotest:kotest-runner-junit5:4.6.3")
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}

gradlePlugin {
  plugins {
    create("extract-plugin") {
      id = "uk.org.lidalia.extract-plugin"
      implementationClass =
        "uk.org.lidalia.gradle.plugins.extractplugin.ExtractPluginPlugin"
    }
  }
}

idea {
  setPackagePrefix("uk.org.lidalia.gradle.plugins.extractplugin")
}
