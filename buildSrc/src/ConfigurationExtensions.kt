import org.gradle.api.artifacts.Configuration
import org.gradle.internal.deprecation.DeprecatableConfiguration

fun Configuration.isDeprecated(): Boolean =
  if (this is DeprecatableConfiguration) {
    resolutionAlternatives != null
  } else {
    false
  }
