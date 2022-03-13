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

  testFixturesImplementation(libs.smack.tcp)
  testFixturesImplementation(libs.smack.extensions)
  testFixturesImplementation(libs.kotest.assertionsCore)
  testFixturesImplementation(libs.kotest.assertionsShared)
  constraints {
    testFixturesImplementation(libs.mockk.core)
  }

  testFixturesRuntimeOnly(libs.smack.java8)

  testImplementation("io.kotest:kotest-framework-api-jvm:5.1.0")
}

idea {
  setPackagePrefix("goos.auction.xmpp")
}
