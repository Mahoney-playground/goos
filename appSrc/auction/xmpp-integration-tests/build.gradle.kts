plugins {
  kotlin("jvm")
  id("alt-application-plugin")
}

dependencies {
  api(kotest("framework-api-jvm"))

  implementation(kotest("framework-api"))
  implementation(kotlinCoroutines("core"))
  implementation(mockk)
  implementation(mockk("dsl-jvm"))
  implementation(project(":auction-api"))
  implementation(project(":auction-xmpp"))
  implementation(project(":xmpp-test-support"))
  implementation(project(":testlauncher"))
  implementation(project(":kotlinlangext"))
  constraints {
    implementation(mockk)
  }
}

application {
  mainClassName = "goos.MainKt"
}
