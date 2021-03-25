plugins {
  kotlin("jvm") version "1.4.31"
  id("org.jmailen.kotlinter") version "3.2.0"
  id("uk.org.lidalia.idea-ext")
  id("uk.org.lidalia.kotlin-flat")
}

repositories {
  mavenCentral()
  maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

group = "uk.org.lidalia"
version = "0.1.0"

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
  implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.3")
}

idea {
  setPackagePrefix("uk.org.lidalia.indexhtml")
}
