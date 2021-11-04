package uk.org.lidalia.gradle.plugins.reportaggregator

class CommandFailedException(
  val failure: Failure
) : RuntimeException(failure.toString())
