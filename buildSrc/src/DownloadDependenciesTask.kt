import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class DownloadDependenciesTask : DefaultTask() {

  @TaskAction
  fun downloadDependencies() = project.configurations
    .filter { it.isCanBeResolved && !it.isDeprecated() }
    .forEach { it.resolve() }
}
