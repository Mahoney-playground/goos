plugins {
  kotlin("jvm")
  `java-test-fixtures`
}

dependencies {
  implementation(kotlin("stdlib"))

  testFixturesImplementation(kotlin("stdlib"))
  testFixturesImplementation(kotest("framework-api"))
  testFixturesImplementation(kotest("assertions-core"))
  testFixturesImplementation(kotest("assertions-core-jvm"))
}

idea {
  setPackagePrefix("goos.ui.api")
}
