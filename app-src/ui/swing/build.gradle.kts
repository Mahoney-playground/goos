plugins {
  kotlin("jvm")
  `java-test-fixtures`
  id("uk.org.lidalia.idea-ext-plugin")
}

dependencies {
  api(project(":ui-api"))

  testFixturesApi(libs.selenium.api)
  testFixturesApi(libs.selenium.remoteDriver)

  testFixturesImplementation(libs.kotest.assertionsCore)
  testFixturesImplementation(libs.kotest.assertionsSharedJvm)
  testFixturesImplementation(libs.coroutines.core)
  testFixturesImplementation(libs.coroutines.coreJvm)
  testFixturesApi(testFixtures(project(":ui-api")))

  testImplementation(libs.marathon.javaDriver)
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
