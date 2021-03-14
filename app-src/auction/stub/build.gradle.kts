plugins {
  kotlin("jvm")
  `java-test-fixtures`
  id("lidalia-idea-plugin")
}

dependencies {

  api(project(":auction-api"))
  api(project(":auction-sol"))

  testImplementation(libs.kotest.assertionsCore)
  testImplementation(libs.kotest.assertionsShared)

  testFixturesApi(testFixtures(project(":auction-api")))
  testFixturesImplementation(libs.kotest.assertionsCore)
  testFixturesImplementation(libs.kotest.assertionsCoreJvm)
  testFixturesImplementation(libs.kotest.assertionsShared)
  constraints {
    testFixturesImplementation(libs.mockk.core)
  }
}

idea {
  setPackagePrefix("goos.auction.stub")
}
