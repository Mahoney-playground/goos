plugins {
  kotlin("jvm")
}

dependencies {
  api(projects.uiApi)
  api(projects.auctionApi)
  api(projects.kotlinlangext)
  testImplementation(libs.mockk.core)
  testImplementation(libs.mockk.dslJvm)
  testImplementation(libs.kotest.assertionsSharedJvm)
  testImplementation(libs.kotest.frameworkApiJvm)
  testImplementation(libs.kotest.commonJvm)
}

idea {
  setPackagePrefix("goos.core")
}
