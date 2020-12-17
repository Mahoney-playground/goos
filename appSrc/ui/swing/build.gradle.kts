plugins {
  kotlin("jvm")
  `java-test-fixtures`
}

dependencies {
  api(project(":ui-api"))

  testFixturesApi(selenium("api"))
  testFixturesApi(selenium("remote-driver"))

  testFixturesImplementation(project(":seleniumext"))
  testFixturesImplementation(kotest("assertions-core"))
  testFixturesImplementation(kotest("assertions-shared-jvm"))
  testFixturesImplementation(kotlinCoroutines("core"))
  testFixturesImplementation(kotlinCoroutines("core-jvm"))
  testFixturesApi(testFixtures(project(":ui-api")))

  testImplementation(project(":seleniumext"))
  testImplementation(marathon("java-driver"))
}

idea {
  setPackagePrefix("goos.ui.swing")
}

tasks {

  named<Test>("test") {
    // silence warnings due to using marathon java agent
    jvmArgs(
      "--add-exports", "java.desktop/sun.awt=ALL-UNNAMED"
    )
  }
}
