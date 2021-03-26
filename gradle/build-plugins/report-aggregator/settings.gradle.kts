includeBuild("../idea-ext")
includeBuild("../kotlin-flat")
includeBuild("../../../libraries/indexhtml") {
  dependencySubstitution {
    substitute(module("uk.org.lidalia:indexhtml")).with(project(":"))
  }
}
