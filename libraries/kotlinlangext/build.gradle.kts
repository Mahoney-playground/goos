plugins {
  kotlin("jvm")
  id("lidalia-idea-plugin")
}

group = "uk.org.lidalia"

dependencies {
  api(libs.coroutines.coreJvm)

  implementation(libs.coroutines.core)
}

idea {
  setPackagePrefix("uk.org.lidalia.kotlinlangext")
}
