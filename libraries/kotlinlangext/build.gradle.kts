plugins {
  kotlin("jvm")
}

group = "uk.org.lidalia"

dependencies {
  api(libs.coroutines.corejvm)

  implementation(libs.coroutines.core)
}

idea {
  setPackagePrefix("uk.org.lidalia.kotlinlangext")
}
