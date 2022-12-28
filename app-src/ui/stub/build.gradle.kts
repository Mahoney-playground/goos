plugins {
  kotlin("jvm")
  `java-test-fixtures`
}

dependencies {

  api(projects.uiApi)
  implementation(projects.uiCommon)

  testImplementation(libs.kotest.frameworkApiJvm)

  testFixturesApi(testFixtures(projects.uiApi))
  testFixturesImplementation(libs.kotest.assertionsCore)
  testFixturesImplementation(libs.kotest.assertionsShared)
  constraints {
    testFixturesImplementation(libs.mockk.core)
  }
}

idea {
  setPackagePrefix("goos.ui.stub")
}
