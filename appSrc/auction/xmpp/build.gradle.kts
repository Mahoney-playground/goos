plugins {
  kotlin("jvm")
  `java-test-fixtures`
}

dependencies {

  api(project(":auction-api"))
  implementation(project(":auction-sol"))

  implementation(kotlin("stdlib"))
  implementation(smack("tcp"))
  implementation(smack("core"))
  implementation(smack("im"))
  implementation(jxmpp("jid"))

  runtimeOnly(smack("extensions"))
  runtimeOnly(smack("java7"))

  testFixturesApi(testFixtures(project(":auction-api")))
  testFixturesApi(kotlin("stdlib"))
  testFixturesApi(smack("core"))
  testFixturesApi(smack("im"))

  testFixturesImplementation(smack("tcp"))
  testFixturesImplementation(smack("extensions"))
  testFixturesImplementation(jxmpp("jid"))
  testFixturesImplementation(kotest("assertions-core"))
  testFixturesImplementation(kotest("assertions-shared"))
  testFixturesImplementation(kotest("assertions-shared-jvm"))
  constraints {
    testFixturesImplementation(mockk)
  }

  testFixturesRuntimeOnly(smack("java7"))
}

idea {
  setPackagePrefix("goos.auction.xmpp")
}
