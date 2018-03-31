/**
 * Created by haipham on 29/3/18.
 */

/**
 * This represents something that can be converted to [Option].
 */
interface OptionConvertibleType<T> {
  /**
   * Convert to [Option].
   */
  fun asOption(): Option<T>
}

/**
 * This represents a type of [Option].
 */
interface OptionType<T>: OptionConvertibleType<T>, TryConvertibleType<T> {
  val value: T?

  /**
   * Check if [value] is available.
   */
  val isSome: Boolean get() = value != null

  /**
   * Check if [value] is null.
   */
  val isNothing: Boolean get() = value == null

  /**
   * Get [value] or return [fallback] if it's not available.
   */
  fun getOrElse(fallback: T): T = value ?: fallback

  /**
   * Get [value] or invoke a [selector] to get a fallback value.
   */
  @Throws(Exception::class)
  fun getOrElse(selector: () -> T): T {
    val value = this.value
    return if (value != null) value else selector()
  }

  /**
   * Return the current [OptionType] or [fallback] if [isNothing] is true.
   */
  fun someOrElse(fallback: OptionConvertibleType<T>): Option<T> {
    return if (isSome) this.asOption() else fallback.asOption()
  }

  /**
   * Return the current [OptionType] or invoke [selector] to get a fallback
   * [OptionConvertibleType].
   */
  fun someOrElse(selector: () -> OptionConvertibleType<T>): Option<T> {
    return try {
      if (isSome) this.asOption() else selector().asOption()
    } catch (e: Exception) {
      Option.nothing<T>()
    }
  }

  /**
   * Get the current [value] or throw an [Exception].
   */
  @Throws(Exception::class)
  fun getOrThrow(): T {
    return value ?: throw Exception(valueError)
  }
}

internal val <T> OptionType<T>.valueError: String
  get() = "Value not available for ${this.javaClass}"

/**
 * Abstract [Option] class.
 */
abstract class Option<T> internal constructor(): OptionType<T> {
  companion object {
    /**
     * Return a [Some].
     */
    fun <T> some(value: T): Option<T> = Some(value)

    /**
     * Return a [Nothing].
     */
    fun <T> nothing(): Option<T> = Nothing()

    /**
     * Wrap a nullable [T] value.
     */
    fun <T> wrap(value: T?): Option<T> {
      return if (value != null) some(value) else nothing()
    }

    /**
     * Evaluate [supplier] and return a non-empty [Option] if no error occurs.
     */
    fun <T> evaluate(supplier: () -> T?): Option<T> {
      return try {
        wrap(supplier())
      } catch (e: Exception) {
        nothing()
      }
    }

    /**
     * Zip all inner values of [options] with [selector].
     */
    fun <T, T1> zip(options: Collection<OptionConvertibleType<T>>,
                    selector: (List<T>) -> T1): Option<T1> {
      return try {
        val values = options.map { it.asOption().getOrThrow() }
        wrap(selector(values))
      } catch (e: Exception) {
        nothing()
      }
    }

    /**
     * Zip all inner values of [options] with [selector].
     */
    fun <T, T1> zip(selector: (List<T>) -> T1,
                    vararg options: OptionConvertibleType<T>): Option<T1> {
      return zip(options.asList(), selector)
    }
  }

  override fun asOption(): Option<T> = this

  override fun asTry(): Try<T> = Try.wrap(value, valueError)

  /**
   * Map [T] to [R] and return [Option] with [R] generics.
   */
  fun <R> map(selector: (T) -> R): Option<R> {
    return try {
      val value = getOrThrow()
      val value1 = selector(value)
      wrap(value1)
    } catch (e: Exception) {
      nothing()
    }
  }

  /**
   * Flat-map [T] to another [OptionConvertibleType] with [R] generics.
   */
  fun <R> flatMap(selector: (T) -> OptionConvertibleType<R>): Option<R> {
    return try {
      val value = getOrThrow()
      selector(value).asOption()
    } catch (e: Exception) {
      nothing()
    }
  }

  /**
   * Flat-map [T] to a nullable [R].
   */
  fun <R> flatMapNullable(selector: (T) -> R?): Option<R> {
    return flatMap { wrap(selector(it)) }
  }

  /**
   * Zip with another [OptionConvertibleType] and combine the inner values with
   * [selector] to produce another [Option].
   */
  fun <T1, T2> zipWith(other: OptionConvertibleType<T1>, selector: (T, T1) -> T2): Option<T2> {
    return try {
      val value = getOrThrow()
      val value1 = other.asOption().getOrThrow()
      wrap(selector(value, value1))
    } catch (e: Exception) {
      nothing()
    }
  }

  /**
   * Zip with another nullable [T1] using [selector].
   */
  fun <T1, T2> zipWithNullable(other: T1?, selector: (T, T1) -> T2): Option<T2> {
    return zipWith(Option.wrap(other), selector)
  }

  /**
   * Filter [value] with [selector] and return [nothing] if the check fails.
   */
  fun filter(selector: (T) -> Boolean): Option<T> {
    return try {
      val value = getOrThrow()
      if (selector(value)) this else nothing()
    } catch (e: Exception) {
      nothing()
    }
  }
}

/**
 * This represents a non-empty [Option].
 */
private class Some<T>(override val value: T): Option<T>()

/**
 * This represents an empty [Option].
 */
private class Nothing<T>: Option<T>() {
  override val value: T? = null
}