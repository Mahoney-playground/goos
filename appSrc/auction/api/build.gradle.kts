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
  testFixturesImplementation(kotlinCoroutines("core"))
  testFixturesImplementation(mockk)
  testFixturesImplementation(mockk("dsl-jvm"))
  testFixturesImplementation(project(":kotlinlangext"))
  constraints {
    testFixturesImplementation(mockk)
  }
}

idea {
  setPackagePrefix("goos.auction.api")
}
