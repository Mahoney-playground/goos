plugins {
  kotlin("jvm")
}

dependencies {
  api(projects.auctionApi)
  implementation(projects.kotlinlangext)
  testImplementation(libs.kotest.frameworkApiJvm)
  testImplementation(libs.mockk.core)
  testImplementation(libs.mockk.dslJvm)
}

idea {
  setPackagePrefix("goos.auction.sol")
}
