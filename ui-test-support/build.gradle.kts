plugins {
  kotlin("jvm")
}

dependencies {
  api("org.seleniumhq.selenium:selenium-remote-driver:3.14.0")
  constraints {
    implementation(byteBuddy)
  }

  implementation(kotest("assertions-core"))
  implementation(kotlinCoroutines("core"))
  constraints {
    implementation(mockk)
  }
}
