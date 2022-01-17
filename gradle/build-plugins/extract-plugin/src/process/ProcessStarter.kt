package uk.org.lidalia.gradle.plugins.extractplugin.process

import uk.org.lidalia.gradle.plugins.extractplugin.either.Outcome
import uk.org.lidalia.gradle.plugins.extractplugin.either.orThrow
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

interface ProcessStarter {
  fun run(
    command: CommandInContext,
    outStream: Appendable = System.out,
    errStream: Appendable = System.err,
  ): Process

  fun CommandInContext.execute(
    outStream: Appendable = System.out,
    errStream: Appendable = System.err,
  ): Outcome<Failed, Succeeded> =
    run(
      this,
      outStream,
      errStream
    )
      .await()

  operator fun CommandInContext.invoke(
    outStream: Appendable = Discard,
    errStream: Appendable = System.err,
  ): String =
    execute(
      outStream,
      errStream,
    )
      .orThrow { f -> ProcessFailedException(f) }
      .stdout

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
}
