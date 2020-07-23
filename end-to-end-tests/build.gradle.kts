plugins {
  kotlin("jvm")
  id("alt-application-plugin")
}

dependencies {

  implementation(kotest("core-jvm"))
  implementation(kotest("assertions-core-jvm"))
  implementation(kotlinCoroutines("core"))
  constraints {
    implementation(mockk)
  }

  implementation(project(":ui-test-support"))
  implementation(project(":xmpp-test-support"))
  implementation(project(":testlauncher"))
}

application {
  mainClassName = "goos.MainKt"
}
