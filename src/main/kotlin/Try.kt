/**
 * Created by haipham on 29/3/18.
 */

/**
 * This represents something that can be converted to [Try].
 */
interface TryConvertibleType<T> {
  /**
   * Convert to [Try].
   */
  fun asTry(): Try<T>
}

/**
 * This represents a type of [Try].
 */
interface TryType<T>: MaybeType<T> {
  val error: Exception?
}

/**
 * Check if [TryType.value] is available.
 */
val <T> TryType<T>.isSuccess: Boolean
  get() = isSome

/**
 * Check if [TryType.error] is available.
 */
val <T> TryType<T>.isFailure: Boolean
  get() = isNothing

/**
 * Return the current [TryType] or [fallback] if [TryType.isSuccess] is true.
 */
fun <T> TryType<T>.successOrElse(fallback: TryConvertibleType<T>): Try<T> {
  return if (isSuccess) this.asTry() else fallback.asTry()
}

/**
 * Abstract [Try] class.
 */
abstract class Try<T> internal constructor(): TryType<T> {
  companion object {
    /**
     * Return a [Success].
     */
    fun <T> success(value: T): Try<T> = Success(value)

    /**
     * Return a [Failure].
     */
    fun <T> failure(error: Exception): Try<T> = Failure(error)

    /**
     * Return a [Failure].
     */
    fun <T> failure(error: String = "Invalid value"): Try<T> {
      return Failure(Exception(error))
    }

    /**
     * Wrap a nullable [T] [value], or return [Failure] with [error].
     */
    fun <T> wrap(value: T?, error: Exception): Try<T> {
      return if (value != null) success(value) else failure(error)
    }

    /**
     * Convenience method to call [wrap] with an error message.
     */
    fun <T> wrap(value: T?, error: String = "Invalid value"): Try<T> {
      return wrap(value, Exception(error))
    }
  }

  override fun asMaybe(): Maybe<T> {
    return Maybe.wrap(value)
  }

  override fun asTry(): Try<T> = this

  /**
   * Map [T] to [R] and return [Try] with [R] generics.
   */
  fun <R> map(selector: (T) -> R): Try<R> {
    return try {
      val value = getOrThrow()
      success(selector(value))
    } catch (e: Exception) {
      failure(e)
    }
  }

  /**
   * Flat-map [T] to another [TryConvertibleType] with [R] generics.
   */
  fun <R> flatMap(selector: (T) -> TryConvertibleType<R>): Try<R> {
    return try {
      val value = getOrThrow()
      selector(value).asTry()
    } catch (e: Exception) {
      failure(e)
    }
  }

  /**
   * Flat-map [T] to a nullable [R].
   */
  fun <R> flatMapNullable(selector: (T) -> R?): Try<R> {
    return flatMap { Maybe.wrap(selector(it)) }
  }
}

/**
 * This represents a non-empty [Try].
 */
private class Success<T>(override val value: T): Try<T>() {
  override val error: Exception? = null

  override fun getOrThrow(): T = value
}

/**
 * This represents an error [Try].
 */
private class Failure<T>(override val error: Exception): Try<T>() {
  override val value: T? = null

  override fun getOrThrow(): T = throw error
}