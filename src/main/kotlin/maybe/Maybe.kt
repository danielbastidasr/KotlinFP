package maybe

/**
 * Created by haipham on 29/3/18.
 */

/**
 * This represents something that can be converted to [Maybe].
 */
interface MaybeConvertibleType<T> {
  /**
   * Convert to [Maybe].
   */
  fun asOptional(): Maybe<T>
}

/**
 * This represents a type of [Maybe].
 */
interface MaybeType<T>: MaybeConvertibleType<T> {
  val value: T?
}

/**
 * Check if [MaybeType.value] is available.
 */
val <T> MaybeType<T>.isSome: Boolean
  get() = value != null

/**
 * Check if [MaybeType.value] is null.
 */
val <T> MaybeType<T>.isNothing: Boolean
  get() = value == null

/**
 * Get the current [value] or throw an [Exception].
 */
@Throws(Exception::class)
fun <T> Maybe<T>.getOrThrow(): T {
  return value ?: throw Exception("Value not available for ${this.javaClass}")
}

/**
 * Get [MaybeType.value] or return [fallback] if it's not available.
 */
fun <T> MaybeType<T>.getOrElse(fallback: T): T = value ?: fallback

/**
 * Return the current [MaybeType] or [fallback] if [MaybeType.isNothing]
 * is true.
 */
fun <T> MaybeType<T>.someOrElse(fallback: MaybeConvertibleType<T>): Maybe<T> {
  return if (isSome) this.asOptional() else fallback.asOptional()
}

/**
 * Abstract [Maybe] class.
 */
abstract class Maybe<T> internal constructor(): MaybeType<T> {
  companion object {
    /**
     * Return a [Some].
     */
    fun <T> some(value: T): Maybe<T> = Some(value)

    /**
     * Return a [Nothing].
     */
    fun <T> nothing(): Maybe<T> = Nothing()

    /**
     * Wrap a nullable [T] value.
     */
    fun <T> wrap(value: T?): Maybe<T> {
      return if (value != null) Maybe.some(value) else Maybe.nothing()
    }
  }

  override fun asOptional(): Maybe<T> {
    return this
  }

  /**
   * Map [T] to [R] and return [Maybe] with [R] generics.
   */
  fun <R> map(selector: (T) -> R): Maybe<R> {
    try {
      val value = getOrThrow()
      val value1 = selector(value)
      return Maybe.some(value1)
    } catch (e: Exception) {
      return Maybe.nothing()
    }
  }

  /**
   * Flat-map [T] to another [MaybeConvertibleType] with [R] generics.
   */
  fun <R> flatMap(selector: (T) -> MaybeConvertibleType<R>): Maybe<R> {
    try {
      val value = getOrThrow()
      return selector(value).asOptional()
    } catch (e: Exception) {
      return Maybe.nothing()
    }
  }

  /**
   * Flat-map [T] to a nullable [R].
   */
  fun <R> flatMapNullable(selector: (T) -> R?): Maybe<R> {
    return flatMap { Maybe.wrap(selector(it)) }
  }
}

private class Some<T>(override val value: T): Maybe<T>()

private class Nothing<T>(): Maybe<T>() {
  override val value: T?
    get() = null
}