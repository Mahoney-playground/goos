plugins {
  kotlin("jvm")
  `java-test-fixtures`
}

dependencies {
  api(projects.kotlinlangext)

  testFixturesImplementation(libs.kotest.frameworkApi)
  testFixturesImplementation(libs.kotest.assertionsCore)
  testFixturesImplementation(libs.kotest.assertionsCoreJvm)
  testFixturesImplementation(libs.coroutines.core)
}

idea {
  setPackagePrefix("goos.ui.api")
}
