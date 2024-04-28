package goos.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.CharsetDecoder
import java.nio.charset.CodingErrorAction
import kotlin.math.ceil
import kotlin.math.roundToInt

private suspend fun InputStream.tee(
  destination: SendChannel<ByteArray>,
  outputStream: OutputStream? = null,
  bufferSize: Int = DEFAULT_BUFFER_SIZE,
) {
  val buffer = ByteArray(bufferSize)
  while (true) {
    val count = runInterruptible(Dispatchers.IO) { read(buffer) }
    if (count < 0) break
    destination.send(buffer.copyOf(count))
    if (outputStream != null) {
      runInterruptible(Dispatchers.IO) { outputStream.write(buffer, 0, count) }
    }
  }
}

private suspend fun ReceiveChannel<ByteArray>.toChars(
  destination: SendChannel<CharArray>,
  decoder: CharsetDecoder = Charsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPLACE),
  bufferSize: Int = DEFAULT_BUFFER_SIZE,
) {
  val inBuffer = ByteBuffer.allocate(2 * bufferSize)
  val outBuffer = CharBuffer.allocate(
    ceil(inBuffer.capacity() * decoder.maxCharsPerByte())
      .roundToInt()
  )
  for (bytes in this) {
    inBuffer.put(bytes).flip()
    decoder.decode(inBuffer, outBuffer, false).apply { check(isUnderflow, ::toString) }
    inBuffer.compact()
    val chars = CharArray(outBuffer.flip().remaining())
    outBuffer.get(chars).clear()
    destination.send(chars)
  }
  inBuffer.flip()
  decoder.decode(inBuffer, outBuffer, true).apply { check(isUnderflow, ::toString) }
  decoder.flush(outBuffer).apply { check(isUnderflow, ::toString) }
  if (outBuffer.flip().hasRemaining()) {
    val chars = CharArray(outBuffer.remaining())
    outBuffer.get(chars)
    destination.send(chars)
  }
}

suspend fun main(): Unit = coroutineScope {
  val outBytes = Channel<ByteArray>()
  val errBytes = Channel<ByteArray>()
  val exitCode = async {
    val process = withContext(Dispatchers.IO) {
      ProcessBuilder()
        .command("/bin/sh", "-c", "dd if=/dev/urandom bs=16 count=4")
        .start()
    }
    try {
      coroutineScope {
        launch { process.inputStream.tee(outBytes /* , System.out */) }
          .invokeOnCompletion(outBytes::close)
        launch { process.errorStream.tee(errBytes /* , System.err */) }
          .invokeOnCompletion(errBytes::close)
      }
      runInterruptible { process.waitFor() }
    } finally {
      process.destroy()
    }
  }
  val outChars = Channel<CharArray>()
  val errChars = Channel<CharArray>()
  launch { outBytes.toChars(outChars) }.invokeOnCompletion(outChars::close)
  launch { errBytes.toChars(errChars) }.invokeOnCompletion(errChars::close)
  coroutineScope {
    launch { for (chars in outChars) println("out: ${String(chars)}") }
    launch { for (chars in errChars) println("err: ${String(chars)}") }
  }
  println("exitCode: ${exitCode.await()}")
}
