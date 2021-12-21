plugins {
  kotlin("jvm")
  `java-test-fixtures`
}

dependencies {

  api(projects.auctionApi)

  implementation(projects.auctionSol)
  implementation(libs.smack.core)
  implementation(libs.jxmpp.jid)
  implementation(libs.smack.tcp)
  implementation(libs.smack.im)

  runtimeOnly(libs.smack.extensions)
  runtimeOnly(libs.smack.java7)

  testFixturesApi(testFixtures(projects.auctionApi))
  testFixturesApi(libs.smack.core)
  testFixturesApi(libs.smack.im)

  testFixturesImplementation(libs.smack.tcp)
  testFixturesImplementation(libs.smack.extensions)
  testFixturesImplementation(libs.jxmpp.jid)
  testFixturesImplementation(libs.kotest.assertionsCore)
  testFixturesImplementation(libs.kotest.assertionsShared)
  testFixturesImplementation(libs.kotest.assertionsSharedJvm)
  constraints {
    testFixturesImplementation(libs.mockk.core)
  }

  testFixturesRuntimeOnly(libs.smack.java7)

  testImplementation(libs.kotestextensions.testcontainers)
  testImplementation(libs.testcontainers)
  testImplementation(libs.slf4j.api)
  testImplementation(libs.kotest.frameworkApiJvm)
  testImplementation(projects.testcontainers)
  testImplementation("ch.qos.logback:logback-classic:1.2.9")
}

idea {
  setPackagePrefix("goos.auction.xmpp")
}
