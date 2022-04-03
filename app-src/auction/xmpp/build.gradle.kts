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
  runtimeOnly(libs.smack.java8)

  testFixturesApi(testFixtures(projects.auctionApi))
  testFixturesApi(libs.smack.core)
  testFixturesApi(libs.smack.im)

  testFixturesImplementation(libs.smack.tcp)
  testFixturesImplementation(libs.smack.extensions)
  testFixturesImplementation(libs.jxmpp.jid)
  testFixturesImplementation(libs.kotest.assertionsCore)
  testFixturesImplementation(libs.kotest.assertionsShared)
  constraints {
    testFixturesImplementation(libs.mockk.core)
  }

  testFixturesRuntimeOnly(libs.smack.java8)

  testImplementation(libs.kotest.frameworkApi)
// removed until we reinstate running the xmpp tests locally
//  testImplementation(libs.kotestextensions.testcontainers)
//  testImplementation(libs.testcontainers)
//  testImplementation(libs.slf4j.api)
//  testImplementation(projects.testcontainers)
//  testImplementation(libs.logback.classic)
}

idea {
  setPackagePrefix("goos.auction.xmpp")
}
