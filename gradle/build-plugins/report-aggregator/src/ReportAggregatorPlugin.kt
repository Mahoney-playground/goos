package uk.org.lidalia.gradle.plugins.reportaggregator

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.ReportingBasePlugin
import org.gradle.api.reporting.Reporting
import org.gradle.api.reporting.ReportingExtension
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import java.io.File
import uk.org.lidalia.indexhtml.addIndexFiles
/**
 * <p>A {@link Plugin} which aggregates the reports of child projects into the parent project.</p>
 *
 * <p>This plugin adds the following extension objects to the project:</p>
 *
 * <ul>
 *
 * <li>{@link org.gradle.api.reporting.ReportingExtension}</li>
 *
 * </ul>
 */
@Suppress("unused")
class ReportAggregatorPlugin : Plugin<Project> {

  override fun apply(project: Project) {

    project.pluginManager.apply(ReportingBasePlugin::class.java)

    val allSubProjectReports = project.configurations.create("allSubProjectReports") {
      isCanBeConsumed = false
      isCanBeResolved = true
    }

    addDependencyToAllSubProjectReports(allSubProjectReports, project)

    val aggregateReports = createAggregateReportsTask(project, allSubProjectReports)

    project.subprojects.forEach { subProject ->
      exportReportsAsArtifact(subProject, aggregateReports)
    }
  }

  private fun addDependencyToAllSubProjectReports(
    configuration: Configuration,
    project: Project
  ) {
    project.dependencies {
      project.subprojects.forEach { subProject ->
        configuration.dependencies.add(
          project(
            mapOf(
              "path" to subProject.path,
              "configuration" to "reports"
            )
          )
        )
      }
    }
  }

  private fun createAggregateReportsTask(
    project: Project,
    allSubProjectReports: Configuration
  ): TaskProvider<Task> {
    return project.tasks.register("aggregateReports") {
      fun buildRelativePath(report: File, project: Project, path: File): File {
        val pathWithCurrentProject = path.resolve(project.name)
        val childProject = project.findChildProjectContaining(report)
        return if (childProject == null) {
          val relativeParent =
            if (project.buildDir.contains(report)) project.buildDir else project.projectDir
          pathWithCurrentProject.resolve(report.relativeTo(relativeParent))
        } else {
          buildRelativePath(report, childProject, pathWithCurrentProject)
        }
      }

      doLast {
        val reporting = project.extensions.getByType<ReportingExtension>()
        allSubProjectReports.resolve().forEach { report ->
          val childProject = project.findChildProjectContaining(report)
          if (childProject == null) {
            logger.info(
              "Cannot aggregate report [{}]; it is not in a child project's directory",
              report
            )
          } else {
            val target = buildRelativePath(report, childProject, reporting.baseDir)
            project.copy {
              from(report)
              into(target)
            }
          }
        }
        reporting.baseDir.addIndexFiles()
      }
    }
  }

  private fun exportReportsAsArtifact(
    subProject: Project,
    aggregateReportsTask: TaskProvider<Task>
  ) {
    subProject.afterEvaluate {

      val reports = subProject.configurations.create("reports") {
        isCanBeConsumed = true
        isCanBeResolved = false
      }

      subProject.tasks
        .filter { it is Reporting<*> || it.name.contains("lint", ignoreCase = true) }
        .forEach { task ->
          task.outputs.files.forEach { file ->
            subProject.artifacts {
              add(reports.name, file) {
                builtBy(task)
              }
            }
          }
          task.finalizedBy(aggregateReportsTask)
        }
    }
  }
}

fun File.contains(maybeChild: File): Boolean = maybeChild.absolutePath.startsWith("$absolutePath/")

fun Project.findChildProjectContaining(file: File): Project? =
  childProjects.values.find { it.projectDir.contains(file) }
