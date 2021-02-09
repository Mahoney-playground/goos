plugins {
  kotlin("jvm")
  `java-test-fixtures`
}

dependencies {
  api(project(":kotlinlangext"))

  testFixturesImplementation(kotest("framework-api"))
  testFixturesImplementation(kotest("assertions-core"))
  testFixturesImplementation(kotest("assertions-core-jvm"))
  testFixturesImplementation(libs.coroutines.core)
}

idea {
  setPackagePrefix("goos.ui.api")
}
