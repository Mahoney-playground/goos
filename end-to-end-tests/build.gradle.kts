plugins {
  kotlin("jvm")
  id("alt-application-plugin")
}

dependencies {

  api(kotlin("stdlib"))
  api(kotest("framework-api"))
  api(kotest("framework-api-jvm"))
  api(project(":ui-swing-test-support"))
  api(project(":auction-xmpp-test-support"))

  implementation(kotlinCoroutines("core"))
  constraints {
    implementation(mockk)
  }

  implementation(project(":testlauncher"))
}

application {
  mainClass.set("goos.MainKt")
}
