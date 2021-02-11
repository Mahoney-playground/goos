plugins {
  kotlin("jvm")
  `java-test-fixtures`
}

dependencies {

  api(project(":ui-api"))

  testImplementation(kotest("assertions-core"))
  testImplementation(kotest("assertions-shared"))

  testFixturesApi(testFixtures(project(":ui-api")))
  testFixturesImplementation(kotest("assertions-core"))
  testFixturesImplementation(kotest("assertions-core-jvm"))
  testFixturesImplementation(kotest("assertions-shared"))
  constraints {
    testFixturesImplementation(libs.mockk.core)
  }
}

idea {
  setPackagePrefix("goos.ui.stub")
}
