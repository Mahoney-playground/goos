plugins {
  kotlin("jvm")
  id("uk.org.lidalia.application")
}

dependencies {
  implementation(libs.kotest.frameworkApiJvm)

  implementation(projects.testlauncher)
  implementation(testFixtures(projects.auctionXmpp))

  implementation(libs.kotest.frameworkApi)
}

application {
  mainClass.set("goos.auction.xmpp.integration.MainKt")
}

idea {
  setPackagePrefix("goos.auction.xmpp.integration")
}

// Workaround for https://github.com/autonomousapps/dependency-analysis-android-gradle-plugin/issues/298
dependencyAnalysis {
  issues {
    onUnusedDependencies {
      exclude(":auction-xmpp")
    }
  }
}
