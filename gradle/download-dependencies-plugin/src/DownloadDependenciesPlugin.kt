package uk.org.lidalia.gradle.plugins.downloaddeps

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class DownloadDependenciesPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.tasks.register<DownloadDependenciesTask>("downloadDependencies")
  }
}
