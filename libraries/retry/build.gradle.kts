plugins {
  kotlin("jvm")
}

dependencies {
  implementation(arrow("core-data"))
  implementation(libs.coroutines.core)
  implementation(libs.coroutines.corejvm)
}
