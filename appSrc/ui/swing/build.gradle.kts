plugins {
  kotlin("jvm")
  `java-test-fixtures`
}

dependencies {
  api(project(":ui-api"))

  testFixturesApi(libs.selenium.api)
  testFixturesApi(libs.selenium.remotedriver)

  testFixturesImplementation(kotest("assertions-core"))
  testFixturesImplementation(kotest("assertions-shared-jvm"))
  testFixturesImplementation(libs.coroutines.core)
  testFixturesImplementation(libs.coroutines.corejvm)
  testFixturesApi(testFixtures(project(":ui-api")))

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
