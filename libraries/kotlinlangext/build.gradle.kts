plugins {
  kotlin("jvm")
}

group = "uk.org.lidalia"

dependencies {
  api(libs.coroutines.coreJvm)

  implementation(libs.coroutines.core)
  testImplementation(libs.kotest.assertionsSharedJvm)
  testImplementation(libs.kotest.frameworkApiJvm)
  testImplementation(libs.kotest.commonJvm)
}

idea {
  setPackagePrefix("uk.org.lidalia.kotlinlangext")
}
