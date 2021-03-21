plugins {
  kotlin("jvm")
  id("uk.org.lidalia.idea-ext-plugin")
  `java-test-fixtures`
}

dependencies {
  api(project(":kotlinlangext"))

  testFixturesApi(libs.kotest.frameworkApiJvm)

  testFixturesImplementation(libs.kotest.frameworkApi)
  testFixturesImplementation(libs.kotest.assertionsCore)
  testFixturesImplementation(libs.kotest.assertionsCoreJvm)
  testFixturesImplementation(libs.coroutines.core)
  testFixturesImplementation(libs.mockk.core)
  testFixturesImplementation(libs.mockk.dslJvm)
  testFixturesImplementation(project(":kotlinlangext"))
  constraints {
    testFixturesImplementation(libs.mockk.core)
  }
}

idea {
  setPackagePrefix("goos.auction.api")
}
