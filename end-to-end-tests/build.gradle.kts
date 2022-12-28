plugins {
  kotlin("jvm")
  id("uk.org.lidalia.application")
}

dependencies {

  implementation(testFixtures(projects.uiApi))
  implementation(testFixtures(projects.auctionApi))

  implementation(libs.kotest.frameworkApi)
  implementation(libs.kotest.frameworkApiJvm)
  implementation(projects.uiApi)
  implementation(testFixtures(projects.uiSwing))
  implementation(projects.auctionApi)
  implementation(testFixtures(projects.auctionXmpp))

  constraints {
    implementation(libs.mockk.core)
  }

  implementation(projects.testlauncher)

  testImplementation(projects.uiStub)
  testImplementation(testFixtures(projects.uiStub))
  testImplementation(projects.auctionStub)
  testImplementation(testFixtures(projects.auctionStub))
  testImplementation(projects.core)
}

application {
  mainClass.set("goos.MainKt")
}
