plugins {
  kotlin("jvm")
}

dependencies {
  api(project(":ui-api"))

  testImplementation(marathon("java-driver"))
  testImplementation(project(":ui-test-support"))
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
