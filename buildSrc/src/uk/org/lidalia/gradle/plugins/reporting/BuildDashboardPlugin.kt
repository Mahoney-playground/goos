package uk.org.lidalia.gradle.plugins.reporting

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ReportingBasePlugin
import org.gradle.api.reporting.Reporting
import org.gradle.api.reporting.ReportingExtension

@Suppress("unused")
class BuildDashboardPlugin : Plugin<Project> {

  override fun apply(project: Project) {

    project.pluginManager.apply(ReportingBasePlugin::class.java)

    val buildDashboard = project.tasks
      .register(
        BUILD_DASHBOARD_TASK_NAME,
        GenerateBuildDashboard::class.java
      ) {
        description = "Generates a dashboard of all the reports produced by this build."
        group = "reporting"
        val htmlReport = reports.html
        htmlReport.outputLocation.convention(
          project.layout.projectDirectory.dir(
            project.provider {
              project.extensions
                .getByType(ReportingExtension::class.java)
                .file("buildDashboard")
                .absolutePath
            }
          )
        )
      }

    project.allprojects.forEach { aProject ->
      aProject.tasks.configureEach {
        if (this is Reporting<*> && name != BUILD_DASHBOARD_TASK_NAME) {
          finalizedBy(buildDashboard)
        }
      }
    }
  }

  companion object {
    const val BUILD_DASHBOARD_TASK_NAME = "buildDashboard"
  }
}
