import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

open class DownloadDependenciesTask : DefaultTask() {

  @InputFiles
  val inputs = project.configurations.names
    .map { project.configurations.getAt(it) }
    .filter { it.isCanBeResolved && !it.isDeprecated() }

  @OutputFile
  val result = project.buildDir.resolve("download_dependencies_result.txt")

  @TaskAction
  fun downloadDependencies() {
    val allDeps = inputs
      .map { it.resolve().size }
      .sum()
    val message = "Resolved all dependencies: $allDeps"
    result.writeText(message)
    project.logger.lifecycle(message)
  }
}
