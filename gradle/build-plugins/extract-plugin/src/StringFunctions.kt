package uk.org.lidalia.gradle.plugins.extractplugin.stringfunctions

fun String.containsAny(vararg chars: Char): Boolean = any { chars.contains(it) }

fun String.containsAny(chars: Collection<Char>): Boolean = any { chars.contains(it) }

private inline fun String.any(predicate: (Char) -> Boolean) = toCharArray().any(predicate)
