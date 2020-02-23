import org.gradle.api.artifacts.Configuration

fun Configuration.isDeprecated(): Boolean =
  if (this is org.gradle.internal.deprecation.DeprecatableConfiguration) {
    resolutionAlternatives != null
  } else {
    false
  }
