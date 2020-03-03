import org.gradle.api.DefaultTask
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.application
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting

open class AssembleAppTask : DefaultTask() {

  @TaskAction
  fun assembleApp() {
    project.convention
    val applicationPlugin = convention.getByType(JavaApplication::class.java)
    val libPlugin = convention.getByType(JavaPluginConvention::class.java)
    val jar by project.tasks.getting(Jar::class) {
      manifest {
        attributes["Main-Class"] = applicationPlugin.mainClassName
      }
    }
  }
}
