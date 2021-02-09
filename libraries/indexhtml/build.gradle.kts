plugins {
  kotlin("jvm")
}

dependencies {
  implementation(kotlinxHtml("jvm"))
  implementation(libs.coroutines.core)
  implementation(libs.coroutines.corejvm)
}

idea {
  setPackagePrefix("uk.org.lidalia.indexhtml")
}
