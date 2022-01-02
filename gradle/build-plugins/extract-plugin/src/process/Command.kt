package uk.org.lidalia.gradle.plugins.extractplugin.process

import uk.org.lidalia.gradle.plugins.extractplugin.stringfunctions.containsAny
import java.nio.file.Path

sealed class Command {
  abstract fun run(
    dir: Path = Path.of("."),
    env: Map<String, String> = emptyMap(),
    outStream: Appendable = System.out,
    errStream: Appendable = System.err,
  ): Process
  abstract val command: String
  internal fun run(
    processBuilder: ProcessBuilder,
    dir: Path,
    env: Map<String, String>,
    outStream: Appendable,
    errStream: Appendable,
  ): Process {
    processBuilder.environment().putAll(env)
    processBuilder.directory(dir.toFile())
    return Process(
      processBuilder,
      this,
      outStream,
      errStream,
    )
  }
  abstract fun pipe(next: Command): Pipe
  override fun toString(): String = command
}

data class Shell(
  override val command: String,
) : Command() {
  override fun run(
    dir: Path,
    env: Map<String, String>,
    outStream: Appendable,
    errStream: Appendable,
  ): Process = run(
    ProcessBuilder("/usr/bin/env", "sh", "-c", command),
    dir,
    env,
    outStream,
    errStream,
  )

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

  override fun run(
    dir: Path,
    env: Map<String, String>,
    outStream: Appendable,
    errStream: Appendable,
  ): Process = run(
    ProcessBuilder(executable, *args.toTypedArray()),
    dir,
    env,
    outStream,
    errStream,
  )
  override fun pipe(next: Command) = Pipe(listOf(this, next))
}

data class Pipe internal constructor(
  private val commands: List<Command>,
) : Command() {
  override fun run(
    dir: Path,
    env: Map<String, String>,
    outStream: Appendable,
    errStream: Appendable,
  ): Process {
    TODO("not implemented")
  }

  override fun pipe(next: Command) = Pipe(commands + next)

  override val command: String = commands.joinToString(" | ")
}
