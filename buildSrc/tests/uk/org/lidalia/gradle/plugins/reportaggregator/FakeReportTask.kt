package uk.org.lidalia.gradle.plugins.reportaggregator

import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.api.model.ObjectFactory
import org.gradle.api.reporting.Reporting
import org.gradle.api.reporting.ReportingExtension
import org.gradle.api.reporting.SingleFileReport
import org.gradle.api.reporting.internal.TaskGeneratedSingleFileReport
import org.gradle.api.reporting.internal.TaskReportContainer
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.util.ClosureBackedAction
import java.io.File
import javax.inject.Inject
import kotlin.random.Random.Default.nextInt

open class FakeReport @Inject constructor(
  task: Task?,
  callbackActionDecorator: CollectionCallbackActionDecorator?
) : TaskReportContainer<SingleFileReport?>(
  SingleFileReport::class.java, task!!, callbackActionDecorator!!
) {
  init {
    add(
      TaskGeneratedSingleFileReport::class.java,
      "report",
      task
    )
  }

  @Internal
  val theReport: SingleFileReport = getByName("report")!!
}

open class FakeReportTask @Inject constructor(
  objectFactory: ObjectFactory
) : DefaultTask(), Reporting<FakeReport> {

  private val reports = objectFactory.newInstance(FakeReport::class.java, this)

  private val reporting = project.extensions.getByType<ReportingExtension>()

  init {
    reports.filterNotNull().forEach { report ->
      report.required.convention(true)
      report.outputLocation.convention(
        project.layout.projectDirectory.file(project.provider {
          File(reporting.baseDir, name).absolutePath
        })
      )
    }
    project.tasks.named<DefaultTask>("check").get().dependsOn += this
  }

  @TaskAction
  fun run() {
    val reportDir = reports.theReport.destination

    reportDir.mkdirs()
    reportDir.resolve("$name.txt").writeText("Hello ${nextInt()}")
  }

  override fun reports(closure: Closure<*>): FakeReport {
    return reports(ClosureBackedAction(closure))
  }

  override fun reports(configureAction: Action<in FakeReport>): FakeReport {
    configureAction.execute(reports)
    return reports
  }

  @Nested
  override fun getReports(): FakeReport = reports
}
