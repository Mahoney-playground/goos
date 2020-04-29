package uk.org.lidalia.kotlinlangext.strings

fun String.toMap(
  pairDelimiter: Char,
  keyValueSeparator: Char
): Map<String, String> {
  return split(pairDelimiter)
    .map { it.split(keyValueSeparator, limit = 2) }
    .toMap()
}

fun List<List<String>>.toMap(): Map<String, String> {
  return mapNotNull { it.toPair() }.toMap()
}

fun List<String>.toPair(): Pair<String, String>? =
  if (this.size >= 2)
    this[0].trim() to this[1].trim()
  else null
