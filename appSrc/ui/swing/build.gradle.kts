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

  named("test", Test::class) {
    // silence warnings due to using marathon java agent
    jvmArgs(
      "-Xshare:off",
      "--illegal-access=deny",
      "--add-exports", "java.desktop/sun.awt=ALL-UNNAMED"
    )
  }
}
