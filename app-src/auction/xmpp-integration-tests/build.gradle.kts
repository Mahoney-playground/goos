plugins {
  kotlin("jvm")
  id("alt-application-plugin")
}

dependencies {
  implementation(libs.kotest.frameworkApiJvm)

  implementation(libs.coroutines.core)

  implementation(project(":testlauncher"))
  implementation(testFixtures(project(":auction-xmpp")))

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
