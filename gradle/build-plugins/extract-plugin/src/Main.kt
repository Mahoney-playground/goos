package uk.org.lidalia.gradle.plugins.extractplugin

import uk.org.lidalia.gradle.plugins.extractplugin.either.orThrow
import uk.org.lidalia.gradle.plugins.extractplugin.filesystem.toDirectory
import uk.org.lidalia.gradle.plugins.extractplugin.filesystem.toPath
import uk.org.lidalia.gradle.plugins.extractplugin.git.CliGitRepo
import uk.org.lidalia.gradle.plugins.extractplugin.git.GitRepo
import uk.org.lidalia.gradle.plugins.extractplugin.github.CliGitHubAccount
import uk.org.lidalia.gradle.plugins.extractplugin.packages.registry.PackagesRegistry
import uk.org.lidalia.gradle.plugins.extractplugin.process.invoke

fun main(vararg args: String) {

  val relativePathToPlugin = args[0].toPath()
  val parentOfNewRepository = args[1].toDirectory().orThrow()

  val existingRepository = CliGitRepo("git rev-parse --show-toplevel"().toDirectory().orThrow())

  val gitHubAccount = CliGitHubAccount()
  val mavenRegistry = gitHubAccount.mavenRegistry()

  val newPluginRepository = existingRepository.extractGradlePluginToNewGitRepo(
    parentOfNewRepository,
    relativePathToPlugin,
  ).apply {
    configurePackagesRegistry(mavenRegistry)
  }

//  val gitHubProject = newPluginRepository.publishTo(gitHubAccount)
//  gitHubProject.publish()

  println(newPluginRepository)
}

private fun GitRepo.configurePackagesRegistry(packagesRegistry: PackagesRegistry) {
//  TODO("Not yet implemented")
}
