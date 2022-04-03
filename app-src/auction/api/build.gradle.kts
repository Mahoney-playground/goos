plugins {
  kotlin("jvm")
  `java-test-fixtures`
}

dependencies {
  api(projects.kotlinlangext)

  testFixturesApi(libs.kotest.frameworkApi)

  testFixturesImplementation(libs.kotest.assertionsCore)
  testFixturesImplementation(libs.coroutines.core)
  testFixturesImplementation(libs.mockk.core)
  testFixturesImplementation(libs.mockk.dslJvm)
  testFixturesImplementation(projects.kotlinlangext)

  constraints {
    testFixturesImplementation(libs.mockk.core)
  }
}

idea {
  setPackagePrefix("goos.auction.api")
}
