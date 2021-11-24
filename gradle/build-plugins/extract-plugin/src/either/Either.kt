package uk.org.lidalia.gradle.plugins.extractplugin.either

sealed class Either<out L, out R> {
  abstract fun <R2> map(f: (R) -> R2): Either<L, R2>
  abstract fun <L2> mapLeft(f: (L) -> L2): Either<L2, R>

  abstract val isLeft: Boolean
  val isRight: Boolean get() = !isLeft
  abstract fun orNull(): R?
}

data class Left<out L>(val value: L) : Either<L, Nothing>() {
  override val isLeft = true
  override fun <R2> map(f: (Nothing) -> R2): Either<L, R2> = this
  override fun <L2> mapLeft(f: (L) -> L2): Either<L2, Nothing> = Left(f(value))
  override fun orNull() = null
}

data class Right<R>(val value: R) : Either<Nothing, R>() {
  override val isLeft = false
  override fun <R2> map(f: (R) -> R2): Either<Nothing, R2> = Right(f(value))
  override fun <L2> mapLeft(f: (Nothing) -> L2): Either<L2, R> = this
  override fun orNull() = value
}

typealias Outcome<F, S> = Either<F, S>
typealias Failure<F> = Left<F>
typealias Success<S> = Right<S>
val <F, S> Outcome<F, S>.isFailure get() = isLeft
val <F, S> Outcome<F, S>.isSuccess get() = isRight
fun <F> F.failure() = Failure(this)
fun <S> S.success() = Success(this)
fun <F : Throwable, S> Outcome<F, S>.orThrow(): S = when (this) {
  is Failure -> throw value
  is Success -> value
}
fun <F, S> Outcome<F, S>.orThrow(f: (F) -> Throwable): S = mapLeft(f).orThrow()

fun <A> Either<A, A>.join(): A = when (this) {
  is Left<A> -> value
  is Right<A> -> value
}

inline fun <L, R, R2> Either<L, R>.flatMap(f: (R) -> Either<L, R2>): Either<L, R2> =
  when (this) {
    is Right -> f(value)
    is Left -> this
  }