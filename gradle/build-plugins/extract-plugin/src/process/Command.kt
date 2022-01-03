package uk.org.lidalia.gradle.plugins.extractplugin.process

import uk.org.lidalia.gradle.plugins.extractplugin.stringfunctions.containsAny

sealed class Command {
  abstract val command: String
  abstract fun pipe(next: Command): Pipe
  override fun toString(): String = command
}

data class Shell(
  override val command: String,
) : Command() {
  override fun pipe(next: Command) = Pipe(listOf(this, next))
}

private val charsRequiringEscapeInShell: Set<Char> = setOf(
  ' ', '"', '#', '$', '&', '(', ')', ';', '<', '>', '?', '[', ']', '`', '{', '|', '}', '~',
  '\\', '\'', '\t', '\r', '\n',
)

data class Exec(
  val executable: String,
  val args: List<String>,
) : Command() {

  constructor(executable: String, vararg args: String) : this(executable, args.toList())

  override val command: String by lazy {
    (listOf(executable) + args).joinToString(" ") { it.shellEscape() }
  }

  private fun String.shellEscape() = if (containsAny(charsRequiringEscapeInShell)) {
    "'${replace("'", "'\\''")}'"
  } else this

  override fun pipe(next: Command) = Pipe(listOf(this, next))
}

data class Pipe internal constructor(
  private val commands: List<Command>,
) : Command() {

  override fun pipe(next: Command) = Pipe(commands + next)

  override val command: String = commands.joinToString(" | ")
}
