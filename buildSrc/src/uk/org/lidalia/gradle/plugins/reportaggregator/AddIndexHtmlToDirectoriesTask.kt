package uk.org.lidalia.gradle.plugins.reportaggregator

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import uk.org.lidalia.indexhtml.addIndexFiles

@Suppress("unused")
open class AddIndexHtmlToDirectoriesTask(
  @Input
  val input: DirectoryProperty
) : DefaultTask() {

  @TaskAction
  fun run() {
    input.get().asFile.addIndexFiles()
  }
}
