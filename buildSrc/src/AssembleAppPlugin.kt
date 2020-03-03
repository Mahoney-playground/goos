import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.distribution.Distribution
import org.gradle.api.file.CopySpec
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.ApplicationPluginConvention
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Jar
import java.util.concurrent.Callable

open class AssembleAppPlugin : Plugin<Project> {

  override fun apply(project: Project) {

    project.pluginManager.apply(ApplicationPlugin::class.java)

    val applicationConvention = project.convention.getPlugin(ApplicationPluginConvention::class.java)
    val javaPluginConvention = project.convention.getPlugin(JavaPluginConvention::class.java)

    addAssembleAppTask(project, applicationConvention, javaPluginConvention)
    configureJarTask(
      project.tasks.named("jar", Jar::class.java),
      applicationConvention,
      javaPluginConvention
    )
  }

  private fun configureJarTask(
    jarTask: TaskProvider<Jar>,
    applicationConvention: ApplicationPluginConvention,
    javaPluginConvention: JavaPluginConvention
  ) {
    val classpath = javaPluginConvention.sourceSets.getByName(MAIN_SOURCE_SET_NAME).runtimeClasspath
    jarTask.configure {
      manifest {
        attributes["Main-Class"] = project.provider(applicationConvention::getMainClassName)
        attributes["Class-Path"] = classpath.map { "deps/${it.name}" }.joinToString(" ")
      }
    }
  }

  private fun addAssembleAppTask(
    project: Project,
    applicationConvention: ApplicationPluginConvention,
    javaPluginConvention: JavaPluginConvention
  ) {
    project.tasks.register(
      "assembleApp",
      Sync::class.java
    ) {

      description = "Runs this project as a JVM application"
      group = ApplicationPlugin.APPLICATION_GROUP
    }
  }

  private fun configureDistribution(
    project: Project,
    mainDistribution: Distribution,
    pluginConvention: ApplicationPluginConvention
  ): CopySpec {
    val distSpec = mainDistribution.contents
    val jar = project.tasks.named(JavaPlugin.JAR_TASK_NAME)
    val libChildSpec = project.copySpec()
    libChildSpec.into("lib")
    libChildSpec.from(jar)
    libChildSpec.from(project.configurations.named(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME))
    val childSpec = project.copySpec()
    childSpec.from(project.file("src/dist"))
    childSpec.with(libChildSpec)
    distSpec.with(childSpec)
    distSpec.with(pluginConvention.applicationDistribution)
    return distSpec
  }
}
