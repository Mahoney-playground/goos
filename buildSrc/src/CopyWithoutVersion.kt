import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.gradle.api.attributes.Attribute
import org.gradle.api.file.CopySpec

fun CopySpec.copyWithoutVersion(
  from: Configuration,
  into: Any
) {
  from.incoming.artifacts.forEach { artifact ->
    this@copyWithoutVersion.from(artifact.file)
    this@copyWithoutVersion.into(into)
    rename { originalName ->
      artifact.filenameWithoutVersion() ?: originalName
    }
  }
}

private val artifactType = Attribute.of("artifactType", String::class.java)

private fun ResolvedArtifactResult.filenameWithoutVersion(): String? {
  @Suppress("UnstableApiUsage")
  val owner = variant.owner
  val type = variant.attributes.getAttribute(artifactType)
  return if (owner is ModuleComponentIdentifier && type != null) {
    "${owner.module}.$type"
  } else {
    null
  }
}
