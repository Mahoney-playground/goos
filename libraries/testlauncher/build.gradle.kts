plugins {
  kotlin("jvm")
}

dependencies {
  implementation(kotest("core-jvm"))
  implementation(kotest("runner-junit5-jvm"))
  constraints {
    implementation(mockk)
  }
  implementation("org.junit.platform:junit-platform-console:1.6.0")

  implementation(project(":clioptions"))
  implementation(project(":indexhtml"))
}
