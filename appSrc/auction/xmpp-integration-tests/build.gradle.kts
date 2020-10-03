plugins {
  kotlin("jvm")
  id("alt-application-plugin")
}

dependencies {
  api(kotest("framework-api-jvm"))

  implementation(kotlin("stdlib"))
  implementation(kotlinCoroutines("core"))

  implementation(project(":testlauncher"))
  implementation(project(":auction-api"))
  implementation(testFixtures(project(":auction-api")))
  implementation(project(":auction-xmpp"))
  implementation(testFixtures(project(":auction-xmpp")))

  implementation(kotest("framework-api"))
  implementation(kotest("assertions-core"))
}

application {
  mainClass.set("goos.MainKt")
}
