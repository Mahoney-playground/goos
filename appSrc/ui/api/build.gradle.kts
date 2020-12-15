plugins {
  kotlin("jvm")
  `java-test-fixtures`
}

dependencies {
  api(project(":kotlinlangext"))
  implementation(kotlin("stdlib"))

  testFixturesImplementation(kotlin("stdlib"))
  testFixturesImplementation(kotest("framework-api"))
  testFixturesImplementation(kotest("assertions-core"))
  testFixturesImplementation(kotest("assertions-core-jvm"))
  testFixturesImplementation(kotlinCoroutines("core"))
}

idea {
  setPackagePrefix("goos.ui.api")
}
