package uk.org.lidalia.gradle.plugins.application

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.ApplicationPluginConvention
import org.gradle.api.plugins.JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Jar

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
          val baseClassPath = project.configurations.getByName(RUNTIME_CLASSPATH_CONFIGURATION_NAME)
//          baseClassPath.partition { it. }
          baseClassPath
            .joinToString(" ") { "deps/${it.name}" }
        }
        attributes["Main-Class"] = project.provider(applicationConvention::getMainClassName)
        attributes["Class-Path"] = classpath
      }
    }
  }
}
