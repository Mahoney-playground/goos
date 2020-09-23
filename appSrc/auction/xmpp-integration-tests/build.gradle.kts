plugins {
  kotlin("jvm")
  id("alt-application-plugin")
}

dependencies {
  api(kotest("framework-api-jvm"))

  implementation(kotlin("stdlib"))
  implementation(kotest("framework-api"))
  implementation(kotlinCoroutines("core"))
  implementation(mockk)
  implementation(mockk("dsl-jvm"))
  implementation(project(":auction-api"))
  implementation(project(":auction-xmpp"))
  implementation(project(":auction-xmpp-test-support"))
  implementation(project(":testlauncher"))
  implementation(project(":kotlinlangext"))
  constraints {
    implementation(mockk)
  }
}

application {
  mainClass.set("goos.MainKt")
}
