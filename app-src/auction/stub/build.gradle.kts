plugins {
  kotlin("jvm")
  `java-test-fixtures`
}

dependencies {

  api(projects.auctionApi)
  api(projects.auctionSol)

  testImplementation(libs.kotest.assertionsCore)
  testImplementation(libs.kotest.assertionsShared)
  testImplementation(libs.kotest.frameworkApiJvm)

  testFixturesApi(testFixtures(projects.auctionApi))
  testFixturesImplementation(libs.kotest.assertionsCore)
  testFixturesImplementation(libs.kotest.assertionsShared)
  constraints {
    testFixturesImplementation(libs.mockk.core)
  }
}

idea {
  setPackagePrefix("goos.auction.stub")
}
