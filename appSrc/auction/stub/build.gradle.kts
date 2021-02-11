plugins {
  kotlin("jvm")
  `java-test-fixtures`
}

dependencies {

  api(project(":auction-api"))
  api(project(":auction-sol"))

  testImplementation(kotest("assertions-core"))
  testImplementation(kotest("assertions-shared"))

  testFixturesApi(testFixtures(project(":auction-api")))
  testFixturesImplementation(kotest("assertions-core"))
  testFixturesImplementation(kotest("assertions-core-jvm"))
  testFixturesImplementation(kotest("assertions-shared"))
  constraints {
    testFixturesImplementation(libs.mockk.core)
  }
}

idea {
  setPackagePrefix("goos.auction.stub")
}
