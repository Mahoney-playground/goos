includeBuild("../idea-ext")
includeBuild("../kotlin-flat")
includeBuild("../../shared-libraries/indexhtml") {
  dependencySubstitution {
    substitute(module("uk.org.lidalia:indexhtml")).with(project(":"))
  }
}
