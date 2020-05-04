plugins {
  kotlin("jvm")
}

dependencies {
  api("org.seleniumhq.selenium:selenium-remote-driver:3.14.0")
  constraints {
    api(byteBuddy)
  }

  implementation(kotest("core"))
  implementation(kotlinCoroutines("core"))
  constraints {
    implementation(mockk)
  }
}
