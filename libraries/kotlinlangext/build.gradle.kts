plugins {
  kotlin("jvm")
  id("uk.org.lidalia.idea-ext-plugin")
}

group = "uk.org.lidalia"

dependencies {
  api(libs.coroutines.coreJvm)

  implementation(libs.coroutines.core)
}

idea {
  setPackagePrefix("uk.org.lidalia.kotlinlangext")
}
