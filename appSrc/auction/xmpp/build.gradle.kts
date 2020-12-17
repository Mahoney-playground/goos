plugins {
  kotlin("jvm")
  `java-test-fixtures`
}

dependencies {

  api(project(":auction-api"))
  implementation(project(":auction-sol"))

  implementation(smack("tcp"))
  implementation(smack("core"))
  implementation(smack("im"))
  implementation(jxmpp("jid"))

  runtimeOnly(smack("extensions"))
  runtimeOnly(smack("java7"))

  testFixturesApi(testFixtures(project(":auction-api")))
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

  testImplementation(kotest("extensions-testcontainers"))
  testImplementation(project(":testcontainers"))
  testImplementation("ch.qos.logback:logback-classic:1.2.3")
}

idea {
  setPackagePrefix("goos.auction.xmpp")
}
