package uk.org.lidalia.gradle.plugins.extractplugin.github

import uk.org.lidalia.gradle.plugins.extractplugin.git.GitRemote
import uk.org.lidalia.gradle.plugins.extractplugin.packages.registry.MavenRegistry

interface GitHubAccount : GitRemote {
  fun mavenRegistry(): MavenRegistry
}
