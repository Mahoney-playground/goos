package uk.org.lidalia.gradle.plugins.extractplugin.github

import uk.org.lidalia.gradle.plugins.extractplugin.packages.registry.MavenRegistry

class CliGitHubAccount : GitHubAccount {
  override fun mavenRegistry(): MavenRegistry {
    return object : MavenRegistry {}
  }
}
