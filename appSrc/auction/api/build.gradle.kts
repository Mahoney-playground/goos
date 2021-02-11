plugins {
  kotlin("jvm")
  `java-test-fixtures`
}

dependencies {
  api(project(":kotlinlangext"))

  testFixturesApi(kotest("framework-api-jvm"))

  testFixturesImplementation(kotest("framework-api"))
  testFixturesImplementation(kotest("assertions-core"))
  testFixturesImplementation(kotest("assertions-core-jvm"))
  testFixturesImplementation(libs.coroutines.core)
  testFixturesImplementation(libs.mockk.core)
  testFixturesImplementation(libs.mockk.dsljvm)
  testFixturesImplementation(project(":kotlinlangext"))
  constraints {
    testFixturesImplementation(libs.mockk.core)
  }
}

idea {
  setPackagePrefix("goos.auction.api")
}
