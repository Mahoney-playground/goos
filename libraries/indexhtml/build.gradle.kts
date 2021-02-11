plugins {
  kotlin("jvm")
}

dependencies {
  implementation(libs.kotlinxHtml.jvm)
  implementation(libs.coroutines.core)
  implementation(libs.coroutines.corejvm)
}

idea {
  setPackagePrefix("uk.org.lidalia.indexhtml")
}
