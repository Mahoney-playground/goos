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
