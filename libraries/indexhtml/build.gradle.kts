plugins {
  kotlin("jvm") version "1.5.30"
  id("org.jmailen.kotlinter") version "3.5.0"
  id("uk.org.lidalia.idea-ext")
  id("uk.org.lidalia.kotlin-flat")
}

repositories {
  mavenCentral()
}

group = "uk.org.lidalia"
version = "0.1.0"

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
  implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.3")
}

idea {
  setPackagePrefix("uk.org.lidalia.indexhtml")
}
