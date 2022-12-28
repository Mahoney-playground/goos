includeBuild("../idea-ext")
includeBuild("../kotlin-flat")
includeBuild("../../../libraries/indexhtml") {
  dependencySubstitution {
    substitute(module("uk.org.lidalia:indexhtml")).using(project(":"))
  }
}
pluginManagement {
  repositories {
    gradlePluginPortal()
    maven {
      url = uri("https://public.mavenrepo.lidalia.org.uk/releases")
    }
  }
}
