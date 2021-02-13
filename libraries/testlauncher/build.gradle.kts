plugins {
  kotlin("jvm")
}

dependencies {
  implementation(libs.kotest.runnerJunit5)
  constraints {
    implementation(libs.mockk.core)
  }
  implementation("org.junit.platform:junit-platform-console:1.7.0")

  implementation(project(":clioptions"))
  implementation(project(":indexhtml"))
}
