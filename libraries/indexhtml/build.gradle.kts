plugins {
  kotlin("jvm")
  id("uk.org.lidalia.idea-ext-plugin")
}

dependencies {
  implementation(libs.kotlinxHtml.jvm)
  implementation(libs.coroutines.core)
  implementation(libs.coroutines.coreJvm)
}

idea {
  setPackagePrefix("uk.org.lidalia.indexhtml")
}
