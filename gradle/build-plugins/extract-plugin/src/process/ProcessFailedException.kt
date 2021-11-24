package uk.org.lidalia.gradle.plugins.extractplugin.process

class ProcessFailedException(
  val failure: Failed
) : RuntimeException(failure.toString())
