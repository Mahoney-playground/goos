includeBuild("../idea-ext")
includeBuild("../kotlin-flat")
includeBuild("../download-dependencies")
includeBuild("../../../libraries/indexhtml") {
  dependencySubstitution {
    substitute(module("uk.org.lidalia:indexhtml")).using(project(":"))
  }
}
