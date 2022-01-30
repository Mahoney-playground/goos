package uk.org.lidalia.gradle.plugins.extractplugin.git

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import uk.org.lidalia.gradle.plugins.extractplugin.filesystem.toPath

class CliGitRepoTest : StringSpec({

  dirToDeleteScenarios.forEach { (
    relativePathToExtract,
    pathsToKeep,
    expectedDirToDelete
  ) ->

    "extracting $relativePathToExtract " +
      "when we want to keep $pathsToKeep " +
      "means we delete $expectedDirToDelete" {

        val dirToDelete = CliGitRepo.dirToDelete(
          pathsToKeep = pathsToKeep,
          relativePathToExtract = relativePathToExtract
        )

        dirToDelete shouldBe expectedDirToDelete
      }
  }
})

/* ktlint-disable no-multi-spaces max-line-length paren-spacing */
val dirToDeleteScenarios = table(
  headers("relativePathToExtract",            "pathsToKeep",                        "expectedDirToDelete"             ),
  row(    "gradle",                           emptyList(),                          "gradle"                          ),
  row(    "gradle/build-plugins",             emptyList(),                          "gradle"                          ),
  row(    "gradle/build-plugins/application", emptyList(),                          "gradle"                          ),

  row(    "gradle",                           listOf("gradle"),                     null                              ),
  row(    "gradle/build-plugins",             listOf("gradle"),                     "gradle/build-plugins"            ),
  row(    "gradle/build-plugins/application", listOf("gradle"),                     "gradle/build-plugins"            ),

  row(    "gradle",                           listOf("gradle/build-plugins/other"), null                              ),
  row(    "gradle/build-plugins",             listOf("gradle/build-plugins/other"), null                              ),
  row(    "gradle/build-plugins/application", listOf("gradle/build-plugins/other"), "gradle/build-plugins/application"),

  row(    "gradle",                           listOf("gradle/wrapper"),             null),
  row(    "gradle/build-plugins",             listOf("gradle/wrapper"),             "gradle/build-plugins"            ),
  row(    "gradle/build-plugins/application", listOf("gradle/wrapper"),             "gradle/build-plugins"            ),
).rows.map { (relativePathToExtract, pathsToKeep, expectedDirToDelete, ) ->
  row(relativePathToExtract.toPath(), pathsToKeep.map { it.toPath() }, expectedDirToDelete?.toPath())
}
/* ktlint-enable no-multi-spaces max-line-length paren-spacing */
