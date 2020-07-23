plugins {
  kotlin("jvm")
}

dependencies {
  implementation(smack("core"))
  implementation(smack("tcp"))
  implementation(smack("extensions"))
  implementation(smack("java7"))
  implementation(smack("im"))
  implementation(kotest("assertions-core-jvm"))
  constraints {
    implementation(mockk)
  }
}
