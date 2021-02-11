plugins {
  kotlin("jvm")
}

dependencies {
  implementation(libs.arrow.coredata)
  implementation(libs.coroutines.core)
  implementation(libs.coroutines.corejvm)
}
