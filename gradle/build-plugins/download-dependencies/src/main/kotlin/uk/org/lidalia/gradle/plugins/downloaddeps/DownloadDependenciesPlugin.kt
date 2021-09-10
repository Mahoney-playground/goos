package uk.org.lidalia.gradle.plugins.downloaddeps

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class DownloadDependenciesPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    val task = project.tasks.register<DownloadDependenciesTask>("downloadDependencies")
    if (project.isRoot) {
      project.gradle.includedBuilds.forEach { includedBuild ->
        task.configure {
          val includedBuildTask = includedBuild.task(":downloadDependencies")
          dependsOn(includedBuildTask)
        }
      }
    }
  }
}

private val Project.isRoot get() = parent == null
