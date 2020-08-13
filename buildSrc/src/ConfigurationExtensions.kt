import org.gradle.api.artifacts.Configuration
import org.gradle.internal.deprecation.DeprecatableConfiguration

fun Configuration.isDeprecated(): Boolean =
  this is DeprecatableConfiguration && resolutionAlternatives != null
