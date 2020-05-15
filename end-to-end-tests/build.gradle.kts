plugins {
  kotlin("jvm")
  id("alt-application-plugin")
}

dependencies {

  implementation(kotest("core"))
  implementation(kotest("assertions-core"))
  implementation(kotlinCoroutines("core"))
  constraints {
    implementation(mockk)
  }

  runtimeOnly(marathon("java-agent"))
  implementation(project(":ui-test-support"))
  implementation(project(":xmpp-test-support"))
  implementation(project(":testlauncher"))
}

application {
  mainClassName = "goos.MainKt"
}
