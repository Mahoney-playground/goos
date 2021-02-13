plugins {
  kotlin("jvm")
  `java-test-fixtures`
}

dependencies {

  api(project(":ui-api"))

  testImplementation(libs.kotest.assertionsCore)
  testImplementation(libs.kotest.assertionsShared)

  testFixturesApi(testFixtures(project(":ui-api")))
  testFixturesImplementation(libs.kotest.assertionsCore)
  testFixturesImplementation(libs.kotest.assertionsCoreJvm)
  testFixturesImplementation(libs.kotest.assertionsShared)
  constraints {
    testFixturesImplementation(libs.mockk.core)
  }
}

idea {
  setPackagePrefix("goos.ui.stub")
}
