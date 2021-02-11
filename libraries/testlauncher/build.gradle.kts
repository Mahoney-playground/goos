plugins {
  kotlin("jvm")
}

dependencies {
  implementation(kotest("runner-junit5"))
  constraints {
    implementation(libs.mockk.core)
  }
  implementation("org.junit.platform:junit-platform-console:1.7.0")

  implementation(project(":clioptions"))
  implementation(project(":indexhtml"))
}
