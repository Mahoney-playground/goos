package uk.org.lidalia.gradle.plugins.application

import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UncheckedIOException
import org.gradle.api.distribution.Distribution
import org.gradle.api.distribution.DistributionContainer
import org.gradle.api.distribution.plugins.DistributionPlugin
import org.gradle.api.distribution.plugins.DistributionPlugin.MAIN_DISTRIBUTION_NAME
import org.gradle.api.file.CopySpec
import org.gradle.api.plugins.ApplicationPluginConvention
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPlugin.JAR_TASK_NAME
import org.gradle.api.plugins.JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.plugins.internal.DefaultApplicationPluginConvention
import org.gradle.api.plugins.internal.DefaultJavaApplication
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.application.CreateStartScripts
import java.io.File
import java.util.concurrent.Callable

/**
 *
 * A [Plugin] which runs a project as a Java Application.
 *
 *
 * The plugin can be configured via its companion [ApplicationPluginConvention] object.
 */
class ApplicationPlugin : Plugin<Project> {

  override fun apply(project: Project) {

    project.pluginManager.apply(JavaPlugin::class.java)
    project.pluginManager.apply(DistributionPlugin::class.java)

    val pluginConvention = addExtensions(project)
    val javaPluginConvention = project.convention.getPlugin(JavaPluginConvention::class.java)

    addRunTask(project, pluginConvention, javaPluginConvention)
    addCreateScriptsTask(project, pluginConvention)
    configureInstallTask(
      project.tasks.named(
        DistributionPlugin.TASK_INSTALL_NAME,
        Sync::class.java
      ), pluginConvention
    )

    val distributions =
      project.extensions.getByName("distributions") as DistributionContainer
    val mainDistribution =
      distributions.getByName(MAIN_DISTRIBUTION_NAME)

    configureDistribution(project, mainDistribution, pluginConvention)
  }

  private fun configureInstallTask(
    installTask: TaskProvider<Sync>,
    pluginConvention: ApplicationPluginConvention
  ) {
    installTask.configure {
      doFirst(
        "don't overwrite existing directories",
        PreventDestinationOverwrite(pluginConvention)
      )
    }
  }

  private fun addExtensions(project: Project): ApplicationPluginConvention {
    val pluginConvention: ApplicationPluginConvention =
      DefaultApplicationPluginConvention(project)
    pluginConvention.applicationName = project.name
    project.convention.plugins["application"] = pluginConvention
    project.extensions.create(
      JavaApplication::class.java, "application",
      DefaultJavaApplication::class.java, pluginConvention
    )
    return pluginConvention
  }

  private fun addRunTask(
    project: Project,
    pluginConvention: ApplicationPluginConvention,
    javaPluginConvention: JavaPluginConvention
  ) {
    project.tasks.register(
      TASK_RUN_NAME,
      JavaExec::class.java
    ) {
      description = "Runs this project as a JVM application"
      group = APPLICATION_GROUP
      classpath = javaPluginConvention.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).runtimeClasspath
      conventionMapping.apply {
        map("main") { pluginConvention.mainClassName }
        map("jvmArgs") { pluginConvention.applicationDefaultJvmArgs }
      }
    }
  }

  // @Todo: refactor this task configuration to extend a copy task and use replace tokens
  private fun addCreateScriptsTask(
    project: Project,
    pluginConvention: ApplicationPluginConvention
  ) {
    project.tasks.register(
      TASK_START_SCRIPTS_NAME,
      CreateStartScripts::class.java
    ) {
      description = "Creates OS specific scripts to run the project as a JVM application."
      classpath = project.tasks.getAt(JAR_TASK_NAME).outputs.files.plus(
        project.configurations.getByName(RUNTIME_CLASSPATH_CONFIGURATION_NAME)
      )
      conventionMapping.apply {
        map("mainClassName") { pluginConvention.mainClassName }
        map("applicationName") { pluginConvention.applicationName }
        map("outputDir") { File(project.buildDir, "scripts") }
        map("executableDir") { pluginConvention.executableDir }
        map("defaultJvmOpts") { pluginConvention.applicationDefaultJvmArgs }
      }
    }
  }

  private fun configureDistribution(
    project: Project,
    mainDistribution: Distribution,
    pluginConvention: ApplicationPluginConvention
  ): CopySpec {

    mainDistribution
      .distributionBaseName
      .convention(project.provider { pluginConvention.applicationName })

    val jar = project.tasks.named(JAR_TASK_NAME)
    val startScripts = project.tasks.named(TASK_START_SCRIPTS_NAME)

    val libChildSpec = project.copySpec().apply {
      into("lib")
      from(jar)
      from(project.configurations.named(RUNTIME_CLASSPATH_CONFIGURATION_NAME))
    }

    val binChildSpec = project.copySpec().apply {
      into(Callable<Any> { pluginConvention.executableDir })
      from(startScripts)
      fileMode = 493
    }

    val childSpec = project.copySpec().apply {
      from(project.file("src/dist"))
      with(libChildSpec)
      with(binChildSpec)
    }

    return mainDistribution.contents {
      with(childSpec)
      with(pluginConvention.applicationDistribution)
    }
  }

  companion object {
    const val APPLICATION_PLUGIN_NAME = "application"
    const val APPLICATION_GROUP = APPLICATION_PLUGIN_NAME
    const val TASK_RUN_NAME = "run"
    const val TASK_START_SCRIPTS_NAME = "startScripts"
  }
}

private class PreventDestinationOverwrite internal constructor(
  private val pluginConvention: ApplicationPluginConvention
) : Action<Task> {
  override fun execute(task: Task) {
    val sync = task as Sync
    val destinationDir = sync.destinationDir
    if (destinationDir.isDirectory) {
      val children = destinationDir.list() ?: throw UncheckedIOException("Could not list directory $destinationDir")
      if (children.isNotEmpty()) {
        if (!File(destinationDir, "lib").isDirectory || !File(
            destinationDir,
            pluginConvention.executableDir
          ).isDirectory
        ) {
          throw GradleException(
            """
            The specified installation directory '$destinationDir' is neither empty nor does it contain an installation for '${pluginConvention.applicationName}'.
            If you really want to install to this directory, delete it and run the install task again.
            Alternatively, choose a different installation directory.
            """.trimIndent()
          )
        }
      }
    }
  }
}
/*
    ant.sync(todir: "$buildDir/libs/deps") {
      configurations.runtime.addToAntBuilder(ant, 'fileset', FileCollection.AntType.FileSet)
    }
     */
