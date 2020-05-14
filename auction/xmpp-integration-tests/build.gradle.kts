plugins {
  kotlin("jvm")
  id("alt-application-plugin")
}

dependencies {
  implementation(kotest("core"))
  implementation(kotlinCoroutines("core"))
  implementation(kotlinCoroutines("jdk8"))
  constraints {
    implementation(mockk)
  }
  implementation(mockk)
  implementation(project(":auction-xmpp"))
  implementation(project(":xmpp-test-support"))
  implementation(project(":testlauncher"))
}

application {
  mainClassName = "goos.MainKt"
}
