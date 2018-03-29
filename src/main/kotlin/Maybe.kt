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
  fun asMaybe(): Maybe<T>
}

/**
 * This represents a type of [Maybe].
 */
interface MaybeType<T>: MaybeConvertibleType<T>, TryConvertibleType<T> {
  val value: T?

  /**
   * Get the current [value] or throw an [Exception].
   */
  @Throws(Exception::class)
  fun getOrThrow(): T {
    return value ?: throw Exception(valueError)
  }
}

private val <T> MaybeType<T>.valueError: String
  get() = "Value not available for ${this.javaClass}"

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
 * Get [MaybeType.value] or return [fallback] if it's not available.
 */
fun <T> MaybeType<T>.getOrElse(fallback: T): T = value ?: fallback

/**
 * Return the current [MaybeType] or [fallback] if [MaybeType.isNothing]
 * is true.
 */
fun <T> MaybeType<T>.someOrElse(fallback: MaybeConvertibleType<T>): Maybe<T> {
  return if (isSome) this.asMaybe() else fallback.asMaybe()
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
      return if (value != null) some(value) else nothing()
    }
  }

  override fun asMaybe(): Maybe<T> = this

  override fun asTry(): Try<T> = Try.wrap(value, valueError)

  /**
   * Map [T] to [R] and return [Maybe] with [R] generics.
   */
  fun <R> map(selector: (T) -> R): Maybe<R> {
    return try {
      val value = getOrThrow()
      val value1 = selector(value)
      some(value1)
    } catch (e: Exception) {
      nothing()
    }
  }

  /**
   * Flat-map [T] to another [MaybeConvertibleType] with [R] generics.
   */
  fun <R> flatMap(selector: (T) -> MaybeConvertibleType<R>): Maybe<R> {
    return try {
      val value = getOrThrow()
      selector(value).asMaybe()
    } catch (e: Exception) {
      nothing()
    }
  }

  /**
   * Flat-map [T] to a nullable [R].
   */
  fun <R> flatMapNullable(selector: (T) -> R?): Maybe<R> {
    return flatMap { wrap(selector(it)) }
  }
}

/**
 * This represents a non-empty [Maybe].
 */
private class Some<T>(override val value: T): Maybe<T>()

/**
 * This represents an empty [Maybe].
 */
private class Nothing<T>: Maybe<T>() {
  override val value: T? = null
}