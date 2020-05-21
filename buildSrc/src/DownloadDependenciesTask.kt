import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class DownloadDependenciesTask : DefaultTask() {

  @TaskAction
  fun downloadDependencies() {
    val allDeps = project.configurations
      .filter { it.isCanBeResolved && !it.isDeprecated() }
      .map { it.resolve().size }
      .sum()
    project.logger.lifecycle("Resolved all dependencies: $allDeps")
  }
}
