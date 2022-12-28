includeBuild("../idea-ext")
includeBuild("../kotlin-flat")
pluginManagement {
  repositories {
    gradlePluginPortal()
    maven {
      url = uri("https://public.mavenrepo.lidalia.org.uk/releases")
    }
  }
}
