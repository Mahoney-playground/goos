package uk.org.lidalia.gradle.plugins.application

import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.UncheckedIOException
import org.gradle.api.distribution.Distribution
import org.gradle.api.distribution.DistributionContainer
import org.gradle.api.distribution.plugins.DistributionPlugin
import org.gradle.api.distribution.plugins.DistributionPlugin.MAIN_DISTRIBUTION_NAME
import org.gradle.api.distribution.plugins.DistributionPlugin.TASK_INSTALL_NAME
import org.gradle.api.file.CopySpec
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.provider.Providers
import org.gradle.api.plugins.ApplicationPluginConvention
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPlugin.COMPILE_JAVA_TASK_NAME
import org.gradle.api.plugins.JavaPlugin.JAR_TASK_NAME
import org.gradle.api.plugins.JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.internal.DefaultApplicationPluginConvention
import org.gradle.api.plugins.internal.DefaultJavaApplication
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.application.CreateStartScripts
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.jvm.toolchain.JavaToolchainSpec
import java.io.File
import java.util.concurrent.Callable

/**
 *
 * A [Plugin] which runs a project as a Java Application.
 *
 *
 * The plugin can be configured via its companion [ApplicationPluginConvention]
 * object.
 */
@Suppress("unused", "UnstableApiUsage")
class ApplicationPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    val tasks = project.tasks

    project.pluginManager.apply(JavaPlugin::class.java)
    project.pluginManager.apply(DistributionPlugin::class.java)

    val pluginConvention = addConvention(project)
    val pluginExtension = addExtensions(project, pluginConvention)
    addRunTask(project, pluginExtension, pluginConvention)
    addCreateScriptsTask(project, pluginExtension, pluginConvention)
    configureInstallTask(
      tasks.named(
        TASK_INSTALL_NAME,
        Sync::class.java
      ),
      pluginConvention
    )
    tasks.named("assemble") {
      dependsOn(TASK_INSTALL_NAME)
    }
    configureJavaCompileTask(
      tasks.named(
        COMPILE_JAVA_TASK_NAME,
        JavaCompile::class.java
      ),
      pluginExtension
    )
    configureJarTask(
      tasks.named(JAR_TASK_NAME, Jar::class.java),
      pluginExtension
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

  private fun addConvention(project: Project): ApplicationPluginConvention {
    val pluginConvention = DefaultApplicationPluginConvention(project).apply {
      applicationName = project.name
    }
    project.convention.plugins["application"] = pluginConvention
    return pluginConvention
  }

  private fun addExtensions(
    project: Project,
    pluginConvention: ApplicationPluginConvention
  ) = project.extensions.create(
    JavaApplication::class.java, "application",
    DefaultJavaApplication::class.java, pluginConvention
  )

  private fun addRunTask(
    project: Project,
    pluginExtension: JavaApplication,
    pluginConvention: ApplicationPluginConvention
  ) {
    project.tasks.register(
      TASK_RUN_NAME,
      JavaExec::class.java
    ) {
      description = "Runs this project as a JVM application"
      group = APPLICATION_GROUP
      val runtimeClasspath = project.files().from(
        {
          if (mainModule.isPresent) {
            jarsOnlyRuntimeClasspath(project)
          } else {
            runtimeClasspath(project)
          }
        }
      )
      classpath = runtimeClasspath
      mainModule.set(pluginExtension.mainModule)
      mainClass.set(pluginExtension.mainClass)
      conventionMapping.map("jvmArgs") { pluginConvention.applicationDefaultJvmArgs }

      val javaPluginExtension = project.extensions.getByType(
        JavaPluginExtension::class.java
      )
      modularity.inferModulePath.convention(javaPluginExtension.modularity.inferModulePath)
      javaLauncher.convention(getToolchainTool(project, JavaToolchainService::launcherFor))
    }
  }

  private fun <T> getToolchainTool(
    project: Project,
    toolMapper: (JavaToolchainService, JavaToolchainSpec) -> Provider<T>
  ): Provider<T> {
    val extension = project.extensions.getByType(JavaPluginExtension::class.java)
    val service = project.extensions.getByType(JavaToolchainService::class.java)
    return toolMapper(service, extension.toolchain).orElse(Providers.notDefined())
  }

  private fun runtimeClasspath(project: Project): FileCollection? =
    project.convention.getPlugin(JavaPluginConvention::class.java).sourceSets.getByName(
      MAIN_SOURCE_SET_NAME
    ).runtimeClasspath

  private fun jarsOnlyRuntimeClasspath(project: Project): FileCollection =
    project.tasks.getAt(JAR_TASK_NAME).outputs.files +
      project.configurations.getByName(RUNTIME_CLASSPATH_CONFIGURATION_NAME)

  // @Todo: refactor this task configuration to extend a copy task and use replace tokens
  private fun addCreateScriptsTask(
    project: Project,
    pluginExtension: JavaApplication,
    pluginConvention: ApplicationPluginConvention
  ) {
    project.tasks.register(
      TASK_START_SCRIPTS_NAME,
      CreateStartScripts::class.java
    ) {
      description = "Creates OS specific scripts to run the project as a JVM application."
      classpath = jarsOnlyRuntimeClasspath(project)
      mainModule.set(pluginExtension.mainModule)
      mainClass.set(pluginExtension.mainClass)
      conventionMapping.apply {
        map("applicationName") { pluginConvention.applicationName }
        map("outputDir") { File(project.buildDir, "scripts") }
        map("executableDir") { pluginConvention.executableDir }
        map("defaultJvmOpts") { pluginConvention.applicationDefaultJvmArgs }
      }
      val javaPluginExtension = project.extensions.getByType(JavaPluginExtension::class.java)
      modularity.inferModulePath.convention(javaPluginExtension.modularity.inferModulePath)
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

    val (internalDependencies, externalDependencies) = project.configurations
      .named(RUNTIME_CLASSPATH_CONFIGURATION_NAME)
      .map { configuration ->
        configuration.files
          .partition { it.isIn(project.rootDir) }
      }
      .unzip()

    val libChildSpec = project.copySpec().apply {
      into("lib")
      from(jar)
      with(
        project.copySpec().apply {
          into("internal")
          from(internalDependencies)
        }
      )
      with(
        project.copySpec().apply {
          into("external")
          from(externalDependencies)
        }
      )
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

  private fun configureJavaCompileTask(
    javaCompile: TaskProvider<JavaCompile>,
    pluginExtension: JavaApplication
  ) {
    javaCompile.configure {
      options.javaModuleMainClass.convention(pluginExtension.mainClass)
    }
  }

  private fun configureJarTask(
    jarTask: TaskProvider<Jar>,
    pluginExtension: JavaApplication
  ) {
    jarTask.configure {
      manifest {
        val classpath = project.configurations
          .named(RUNTIME_CLASSPATH_CONFIGURATION_NAME).map { configuration ->
            val (projectDepFiles, externalDepFiles) = configuration.files
              .partition { it.isIn(project.rootDir) }

            val projectDeps = projectDepFiles
              .map { "internal/${it.name}" }
            val externalDeps = externalDepFiles
              .map { "external/${it.name}" }

            (projectDeps + externalDeps).joinToString(" ")
          }
        attributes["Main-Class"] = pluginExtension.mainClass
        attributes["Class-Path"] = classpath
      }
    }
  }

  companion object {
    @Suppress("MemberVisibilityCanBePrivate")
    const val APPLICATION_PLUGIN_NAME = "application"
    const val APPLICATION_GROUP = APPLICATION_PLUGIN_NAME
    const val TASK_RUN_NAME = "run"
    const val TASK_START_SCRIPTS_NAME = "startScripts"
  }
}

private fun <A : Any, B : Any> Provider<Pair<A, B>>.unzip(): Pair<Provider<A>, Provider<B>> =
  map { it.first } to map { it.second }

private class PreventDestinationOverwrite constructor(
  private val pluginConvention: ApplicationPluginConvention
) : Action<Task> {
  override fun execute(task: Task) {
    val sync = task as Sync
    val destinationDir = sync.destinationDir
    if (destinationDir.isDirectory) {
      val children = destinationDir.list()
        ?: throw UncheckedIOException("Could not list directory $destinationDir")
      if (children.isNotEmpty()) {
        val lib = File(destinationDir, "lib")
        val exec = File(destinationDir, pluginConvention.executableDir)
        if (!lib.isDirectory || !exec.isDirectory) {
          throw GradleException(
            nonEmptyDestinationError(
              destinationDir,
              pluginConvention.applicationName
            )
          )
        }
      }
    }
  }
}

private fun nonEmptyDestinationError(
  destinationDir: File,
  applicationName: String
): String =
  """
  The specified installation directory '$destinationDir' is neither empty nor does it contain an
  installation for '$applicationName'.
  If you really want to install to this directory, delete it and run the install task again.
  Alternatively, choose a different installation directory.
  """.trimIndent()

private fun File.isIn(maybeParent: File) = absolutePath.contains(maybeParent.absolutePath + "/")
