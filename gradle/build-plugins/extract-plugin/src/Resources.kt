package uk.org.lidalia.gradle.plugins.extractplugin.resources

import java.net.URL

fun resource(resourceName: String): URL? =
  Thread.currentThread().contextClassLoader.getResource(resourceName)
