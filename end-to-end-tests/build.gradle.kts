plugins {
  kotlin("jvm")
  id("alt-application-plugin")
}

dependencies {

  api(kotlin("stdlib"))
  api(kotest("framework-api"))
  api(kotest("framework-api-jvm"))
  api(testFixtures(project(":auction-api")))
  api(testFixtures(project(":ui-swing")))

  implementation(testFixtures(project(":auction-xmpp")))
  implementation(kotlinCoroutines("core"))
  constraints {
    implementation(mockk)
  }

  implementation(project(":testlauncher"))
}

application {
  mainClass.set("goos.MainKt")
}
