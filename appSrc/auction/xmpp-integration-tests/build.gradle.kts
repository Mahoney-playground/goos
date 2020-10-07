plugins {
  kotlin("jvm")
  id("alt-application-plugin")
}

dependencies {
  api(kotest("framework-api-jvm"))

  implementation(kotlin("stdlib"))
  implementation(kotlinCoroutines("core"))

  implementation(project(":testlauncher"))
  implementation(testFixtures(project(":auction-xmpp")))

  implementation(kotest("framework-api"))
}

application {
  mainClassName = "goos.auction.xmpp.integration.MainKt"
}

idea {
  setPackagePrefix("goos.auction.xmpp.integration")
}
