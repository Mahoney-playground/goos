package uk.org.lidalia.gradle.plugins.extractplugin.process

import uk.org.lidalia.gradle.plugins.extractplugin.either.Outcome
import uk.org.lidalia.gradle.plugins.extractplugin.either.orThrow
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

fun Command.execute(
  dir: Path = Paths.get("."),
  env: Map<String, String> = emptyMap(),
  outStream: Appendable = System.out,
  errStream: Appendable = System.err,
): Outcome<Failed, Succeeded> =
  JavaProcessStarter().run(this, dir, env, outStream, errStream)
    .await()

operator fun Command.invoke(
  dir: Path = Paths.get("."),
  env: Map<String, String> = emptyMap(),
  outStream: Appendable = Discard,
  errStream: Appendable = System.err,
): String =
  execute(
    dir,
    env,
    outStream,
    errStream,
  )
    .orThrow { f -> ProcessFailedException(f) }
    .stdout

operator fun File.invoke(
  vararg args: String,
  dir: Path = Paths.get("."),
  env: Map<String, String> = emptyMap(),
): String =
  Exec(this.absolutePath, args.toList())
    .invoke(dir, env)

operator fun String.invoke(
  dir: Path = Paths.get("."),
  env: Map<String, String> = emptyMap(),
): String = Shell(this).invoke(dir, env)

fun String.execute(
  dir: Path = Paths.get("."),
  env: Map<String, String> = emptyMap(),
): Outcome<Failed, Succeeded> = Shell(this).execute(dir, env)

@Suppress("DANGEROUS_CHARACTERS")
infix fun String.`|`(command: Command): Pipe = Shell(this).pipe(command)

@Suppress("DANGEROUS_CHARACTERS")
infix fun String.`|`(command: String): Pipe = Shell(this).pipe(Shell(command))
