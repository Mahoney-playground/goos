plugins {
  kotlin("jvm")
}

dependencies {
  implementation(libs.kotest.runnerJunit5)
  constraints {
    implementation(libs.mockk.core)
  }
  implementation(libs.junitPlatform.console)

  implementation(projects.clioptions)
  implementation(libs.indexhtml)
}
