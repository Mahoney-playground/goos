import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class DownloadDependenciesTask : DefaultTask() {

  @TaskAction
  fun downloadDependencies() {
    val allDeps = project.configurations.names
      .map { project.configurations.getAt(it) }
      .filter { it.isCanBeResolved && !it.isDeprecated() }
      .map { it.resolve().size }
      .sum()
    println("Resolved all dependencies: $allDeps")
  }
}
