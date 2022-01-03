package uk.org.lidalia.gradle.plugins.extractplugin.process

import uk.org.lidalia.gradle.plugins.extractplugin.either.Outcome
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

fun CommandInContext.execute(
  outStream: Appendable = System.out,
  errStream: Appendable = System.err,
): Outcome<Failed, Succeeded> =
  JavaProcessStarter.run {
    execute(
      outStream,
      errStream
    )
  }

operator fun CommandInContext.invoke(
  outStream: Appendable = Discard,
  errStream: Appendable = System.err,
): String =
  JavaProcessStarter.run {
    invoke(
      outStream,
      errStream,
    )
  }

fun Command.execute(
  dir: Path = Paths.get("."),
  env: Map<String, String> = emptyMap(),
  outStream: Appendable = System.out,
  errStream: Appendable = System.err,
): Outcome<Failed, Succeeded> =
  withContext(dir, env)
    .execute(
      outStream,
      errStream
    )

operator fun Command.invoke(
  dir: Path = Paths.get("."),
  env: Map<String, String> = emptyMap(),
  outStream: Appendable = Discard,
  errStream: Appendable = System.err,
): String =
  withContext(dir, env)
    .invoke(
      outStream,
      errStream,
    )

operator fun File.invoke(
  vararg args: String,
  dir: Path = Paths.get("."),
  env: Map<String, String> = emptyMap(),
): String =
  JavaProcessStarter.run {
    invoke(*args, dir = dir, env = env)
  }

operator fun String.invoke(
  dir: Path = Paths.get("."),
  env: Map<String, String> = emptyMap(),
): String = JavaProcessStarter.run {
  invoke(dir = dir, env = env)
}

fun String.execute(
  dir: Path = Paths.get("."),
  env: Map<String, String> = emptyMap(),
): Outcome<Failed, Succeeded> = JavaProcessStarter.run {
  execute(dir, env)
}

@Suppress("DANGEROUS_CHARACTERS")
infix fun String.`|`(command: Command): Pipe = Shell(this).pipe(command)

@Suppress("DANGEROUS_CHARACTERS")
infix fun String.`|`(command: String): Pipe = Shell(this).pipe(Shell(command))
