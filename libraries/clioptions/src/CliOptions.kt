package uk.org.lidalia.clioptions

fun Array<String>.withDefaultOption(
  shortOption: Char?,
  longOption: String,
  value: String
): Array<String> {

  val alreadyHasOption =
    (shortOption != null && this.contains("-$shortOption")) ||
      this.any { it.startsWith("--$longOption=") }

  return if (alreadyHasOption) this else this + "--$longOption=$value"
}

fun Array<String>.getOption(
  shortOption: Char?,
  longOption: String
): String? {
  val shortOptionIndex = if (shortOption == null) null else { this.indexOfOrNull("-$shortOption") }
  return if (shortOptionIndex != null) {
    this.getOrNull(shortOptionIndex + 1)
  } else {
    find { it.startsWith("--$longOption=") }?.substringAfter("--$longOption=")
  }
}

private fun <T> Array<out T>.indexOfOrNull(element: T): Int? {
  val index = indexOf(element)
  return if (index >= 0) index else null
}
