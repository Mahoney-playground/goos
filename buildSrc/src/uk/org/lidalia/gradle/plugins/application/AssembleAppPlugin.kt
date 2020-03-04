package uk.org.lidalia.gradle.plugins.application

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.ApplicationPluginConvention
import org.gradle.api.plugins.JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Jar
import java.io.File

open class AssembleAppPlugin : Plugin<Project> {

  override fun apply(project: Project) {

    project.pluginManager.apply(ApplicationPlugin::class.java)

    val applicationConvention = project.convention.getPlugin(ApplicationPluginConvention::class.java)

    configureJarTask(
      project.tasks.named("jar", Jar::class.java),
      applicationConvention
    )
  }

  private fun configureJarTask(
    jarTask: TaskProvider<Jar>,
    applicationConvention: ApplicationPluginConvention
  ) {
    jarTask.configure {
      manifest {
        val classpath = project.provider {

          val (projectDepFiles, externalDepFiles) = project.configurations
            .getByName(RUNTIME_CLASSPATH_CONFIGURATION_NAME).files
            .partition {it.isIn(project.rootDir) }

          val projectDeps = projectDepFiles
            .map { "deps/project/${it.name}" }
          val externalDeps = externalDepFiles
            .map { "deps/external/${it.name}" }

          (projectDeps + externalDeps).joinToString(" ")
        }
        attributes["Main-Class"] = project.provider(applicationConvention::getMainClassName)
        attributes["Class-Path"] = classpath
      }
    }
  }
}

private fun File.isIn(maybeParent: File) = absolutePath.contains(maybeParent.absolutePath + "/")
