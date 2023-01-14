rootProject.name = "indexhtml"
includeBuild("../../gradle/build-plugins/kotlin-flat")
pluginManagement {
  repositories {
    gradlePluginPortal()
    maven {
      url = uri("https://public.mavenrepo.lidalia.org.uk/releases")
    }
  }
}
