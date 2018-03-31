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
interface TryType<T>: OptionType<T> {
  val error: Exception?

  /**
   * Check if [value] is available.
   */
  val isSuccess: Boolean get() = isSome

  /**
   * Check if [error] is available.
   */
  val isFailure: Boolean get() = isNothing

  /**
   * Return the current [TryType] or [fallback] if [isSuccess] is true.
   */
  fun successOrElse(fallback: TryConvertibleType<T>): Try<T> {
    return if (isSuccess) this.asTry() else fallback.asTry()
  }

  /**
   * Return the current [TryType] or invoke [selector] to get a fallback
   * [TryConvertibleType].
   */
  fun successOrElse(selector: () -> TryConvertibleType<T>): Try<T> {
    return try {
      if (isSuccess) this.asTry() else selector().asTry()
    } catch (e: Exception) {
      Try.failure(e)
    }
  }
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
     * Convenience method to call [wrap] with [error] message.
     */
    fun <T> wrap(value: T?, error: String = "Invalid value"): Try<T> {
      return wrap(value, Exception(error))
    }

    /**
     * Evaluate [supplier] and return a success [Try] if no error occurs.
     */
    fun <T> evaluate(supplier: () -> T?, error: Exception): Try<T> {
      return try {
        wrap(supplier(), error)
      } catch (e: Exception) {
        failure(e)
      }
    }

    /**
     * Convenience method to call [evaluate] with [error] message.
     */
    fun <T> evaluate(supplier: () -> T?, error: String = "Invalid value"): Try<T> {
      return evaluate(supplier, Exception(error))
    }

    /**
     * Zip all inner values of [tries] with [selector].
     */
    fun <T, T1> zip(tries: Collection<TryConvertibleType<T>>,
                    selector: (List<T>) -> T1): Try<T1> {
      return try {
        val values = tries.map { it.asTry().getOrThrow() }
        wrap(selector(values))
      } catch (e: Exception) {
        failure(e)
      }
    }

    /**
     * Zip all inner values of [tries] with [selector].
     */
    fun <T, T1> zip(selector: (List<T>) -> T1,
                    vararg tries: TryConvertibleType<T>): Try<T1> {
      return zip(tries.asList(), selector)
    }
  }

  override fun asOption(): Option<T> {
    return Option.wrap(value)
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
    return flatMap { Option.wrap(selector(it)) }
  }

  /**
   * Zip with another [TryConvertibleType] using [selector].
   */
  fun <T1, T2> zipWith(other: TryConvertibleType<T1>, selector: (T, T1) -> T2): Try<T2> {
    return try {
      val value = getOrThrow()
      val value1 = other.asTry().getOrThrow()
      Try.wrap(selector(value, value1))
    } catch (e: Exception) {
      Try.failure<T2>(e)
    }
  }

  /**
   * Zip with another nullable [T1] using [selector].
   */
  fun <T1, T2> zipWithNullable(other: T1?, selector: (T, T1) -> T2): Try<T2> {
    return zipWith(Option.wrap(other), selector)
  }

  /**
   * Filter [value] with [selector] and return [failure] if the check fails.
   * [error] will be used if the selector returns false.
   */
  fun filter(selector: (T) -> Boolean, error: Exception): Try<T> {
    return try {
      val value = getOrThrow()
      if (selector(value)) this else failure(error)
    } catch (e: Exception) {
      failure(e)
    }
  }

  /**
   * Convenient method to filter [value] with an [error] message.
   */
  fun filter(selector: (T) -> Boolean, error: String = "Invalid value"): Try<T> {
    return filter(selector, Exception(error))
  }
}

/**
 * This represents a non-empty [Try].
 */
private class Success<T>(override val value: T): Try<T>() {
  override val error: Exception? = null

  @Throws(Exception::class)
  override fun getOrThrow(): T = value
}

/**
 * This represents an error [Try].
 */
private class Failure<T>(override val error: Exception): Try<T>() {
  override val value: T? = null

  @Throws(Exception::class)
  override fun getOrThrow(): T = throw error
}