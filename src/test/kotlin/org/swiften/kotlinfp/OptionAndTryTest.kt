package org.swiften.kotlinfp

import org.testng.Assert
import org.testng.annotations.Test

/**
 * Created by haipham on 29/3/18.
 */

class OptionAndTryTest {
  @Test
  fun test_whenWithSpecificCases_shouldWork() {
    /// Setup
    val o1 = Option.some(1)
    val o2 = Option.nothing<Int>()
    val t1 = Try.success(1)
    val t2 = Try.failure<Int>("Error")
    val t3 = Try.failure<Int>(Throwable("Throwable!"))

    /// When & Then
    when(o1) {
      is Option.Some<Int> -> Assert.assertEquals(o1.value, 1)
      is Option.Nothing<Int> -> Assert.fail("Should not reach here")
    }

    when(o2) {
      is Option.Some<Int> -> Assert.fail("Should not reach here")
      is Option.Nothing<Int> -> Assert.assertTrue(true)
    }

    when(t1) {
      is Try.Success<Int> -> Assert.assertEquals(t1.value, 1)
      is Try.Failure<Int> -> Assert.fail("Should not reach here")
    }

    when(t2) {
      is Try.Success<Int> -> Assert.fail("Should not reach here")
      is Try.Failure<Int> -> Assert.assertEquals(t2.error.message, "Error")
    }

    when(t3) {
      is Try.Success<Int> -> Assert.fail("Should not reach here")
      is Try.Failure<Int> -> Assert.assertEquals(t3.error.message, "Throwable!")
    }
  }

  @Test
  fun test_asOptionAndTry_shouldWork() {
    /// Setup
    val o1: OptionType<Int> = Option.some(1)
    val o2: OptionType<Int> = Option.nothing()
    val t1: OptionType<Int> = Try.success(1)
    val t2: OptionType<Int> = Try.failure()

    /// When & Then
    Assert.assertEquals(o1, o1.asOption())
    Assert.assertEquals(o2, o2.asOption())
    Assert.assertEquals(t1, t1.asTry())
    Assert.assertEquals(t2, t2.asTry())
    Assert.assertEquals(o1.asTry().value, 1)
    Assert.assertTrue(o2.asTry().isFailure)
    Assert.assertEquals(t1.asOption().value, 1)
    Assert.assertTrue(t2.asOption().isNothing)
  }

  @Test
  fun test_isSomeAndIsNothing_shouldWork() {
    /// Setup
    val o1 = Option.some(1)
    val o2 = Option.nothing<Int>()
    val o3 = Option.wrap(1)
    val o4 = Option.wrap<Int>(null)
    val m5 = Option.evaluate { 1 }
    val m6 = Option.evaluate { throw Exception("") }
    val m7 = Option.evaluate { null }
    val t1 = Try.success(1)
    val t2 = Try.failure<Int>("")
    val t3 = Try.wrap(1, "")
    val t4 = Try.wrap<Int>(null, "")
    val t5 = Try.evaluate({ 1 })
    val t6 = Try.evaluate({ throw Exception("Error") })

    /// When & Then
    Assert.assertEquals(o1.value, 1)
    Assert.assertNull(o2.value)
    Assert.assertEquals(o3.value, 1)
    Assert.assertNull(o4.value)
    Assert.assertEquals(m5.value, 1)
    Assert.assertTrue(m6.isNothing)
    Assert.assertTrue(m7.isNothing)
    Assert.assertEquals(t1.value, 1)
    Assert.assertNull(t1.error)
    Assert.assertNull(t2.value)
    Assert.assertNotNull(t2.error)
    Assert.assertEquals(t3.value, 1)
    Assert.assertNull(t3.error)
    Assert.assertNull(t4.value)
    Assert.assertNotNull(t4.error)
    Assert.assertTrue(o1.isSome)
    Assert.assertFalse(o1.isNothing)
    Assert.assertTrue(o2.isNothing)
    Assert.assertFalse(o2.isSome)
    Assert.assertTrue(o3.isSome)
    Assert.assertFalse(o3.isNothing)
    Assert.assertTrue(o4.isNothing)
    Assert.assertFalse(o4.isSome)
    Assert.assertTrue(t1.isSuccess)
    Assert.assertFalse(t1.isFailure)
    Assert.assertTrue(t2.isFailure)
    Assert.assertFalse(t2.isSuccess)
    Assert.assertTrue(t3.isSuccess)
    Assert.assertFalse(t3.isFailure)
    Assert.assertTrue(t4.isFailure)
    Assert.assertFalse(t4.isSuccess)
    Assert.assertEquals(t5.value, 1)
    Assert.assertEquals(t6.error?.message, "Error")
  }

  @Test
  fun test_getOrThrow_shouldWork() {
    /// Setup
    val o1 = Option.some(1)
    val o2 = Option.nothing<Int>()
    val t1 = Try.success(1)
    val t2 = Try.failure<Int>("Error")

    /// When & Then
    Assert.assertEquals(o1.getOrThrow(), 1)

    try {
      o2.getOrThrow()
      Assert.fail("Should not have completed")
    } catch (e: Exception) {}

    Assert.assertEquals(t1.getOrThrow(), 1)

    try {
      t2.getOrThrow()
      Assert.fail("Should not have completed")
    } catch (e: Exception) {
      Assert.assertEquals(e.message, "Error")
    }
  }

  @Test
  fun test_getOrElse_shouldWork() {
    /// Setup
    val o1 = Option.some(1)
    val o2 = Option.nothing<Int>()
    val t1 = Try.success(1)
    val t2 = Try.failure<Int>()

    /// When & Then
    Assert.assertEquals(o1.getOrElse(2), 1)
    Assert.assertEquals(o2.getOrElse(2), 2)
    Assert.assertEquals(o1.getOrElse { throw Exception("") }, 1)
    Assert.assertEquals(o2.getOrElse { 1 }, 1)
    Assert.assertEquals(t1.getOrElse { 2 }, 1)
    Assert.assertEquals(t2.getOrElse { 2 }, 2)
  }

  @Test
  fun test_someOrElseAndSuccessOrElse_shouldWork() {
    /// Setup
    val o1 = Option.some(1)
    val o2 = Option.nothing<Int>()
    val t1 = Try.success(1)
    val t2 = Try.failure<Int>("Error")

    /// When & Then
    Assert.assertEquals(o1.someOrElse(o2).value, 1)
    Assert.assertEquals(o1.someOrElse(t2).value, 1)
    Assert.assertEquals(o2.someOrElse(o1).value, 1)
    Assert.assertEquals(o2.someOrElse(t1).value, 1)
    Assert.assertEquals(o1.someOrElse { o2 }.value, 1)
    Assert.assertEquals(o1.someOrElse { throw Exception("") }.value, 1)
    Assert.assertTrue(o2.someOrElse { throw Exception("") }.isNothing)
    Assert.assertEquals(t1.someOrElse(o2).value, 1)
    Assert.assertEquals(t2.someOrElse(o1).value, 1)
    Assert.assertEquals(t1.successOrElse(t2).value, 1)
    Assert.assertEquals(t2.successOrElse(t1).value, 1)
    Assert.assertEquals(t1.successOrElse { throw Exception("") }.value, 1)
    Assert.assertEquals(t2.successOrElse { t1 }.value, 1)
    Assert.assertEquals(t2.successOrElse { throw Exception("Error") }.error?.message, "Error")
  }

  @Test
  fun test_catchFailures_shouldWork() {
    /// Setup
    val o1 = Option.some(1)
    val o2 = Option.nothing<Int>()
    val t1 = Try.success(1)
    val t2 = Try.failure<Int>("Error")

    /// When & Then
    Assert.assertEquals(o1.catchNothing { throw Exception("") }.value, 1)
    Assert.assertEquals(o2.catchNothing(1).value, 1)
    Assert.assertTrue(o2.catchNothing { throw Exception("") }.isNothing)
    Assert.assertEquals(t1.catchFailure { throw Exception("") }.value, 1)
    Assert.assertEquals(t2.catchFailure(1).value, 1)
    Assert.assertTrue(t2.catchFailure { throw Exception("") }.isFailure)
  }

  @Test
  fun test_map_shouldWork() {
    /// Setup
    val o1 = Option.some(1)
    val o2 = Option.nothing<Int>()
    val t1 = Try.success(1)
    val t2 = Try.failure<Int>("Error")

    /// When
    val o1m = o1.map(Int::toString)
    val o2m = o2.map(Int::toString)
    val o3m = o1.map(Int::toString).map { it + it }
    val t1m = t1.map(Int::toString)
    val t2m = t2.map(Int::toString)

    /// Then
    Assert.assertEquals(o1m.value, "1")
    Assert.assertTrue(o2m.isNothing)
    Assert.assertEquals(o3m.value, "11")
    Assert.assertEquals(t1m.value, "1")
    Assert.assertTrue(t2m.isFailure)
  }

  @Test
  fun test_flatMap_shouldWork() {
    /// Setup
    val o1 = Option.some(1)
    val o2 = Option.nothing<Int>()
    val t1 = Try.success(1)
    val t2 = Try.failure<Int>("Error")

    /// When
    val o1fm = o1.flatMapNullable<Int> { null }
    val o2fm = o2.flatMap { Option.some(1) }
    val t1fm = t1.flatMapNullable<Int> { null }
    val t2fm = t2.flatMap { Try.success(1) }

    /// Then
    Assert.assertTrue(o1fm.isNothing)
    Assert.assertTrue(o2fm.isNothing)
    Assert.assertTrue(t1fm.isFailure)
    Assert.assertTrue(t2fm.isFailure)
  }

  @Test
  fun test_zipAndZipWith_shouldWork() {
    /// Setup
    val o1 = Option.some(1)
    val o2 = Option.nothing<Int>()
    val t1 = Try.success(1)
    val t2 = Try.failure<Int>()

    /// When
    val mz1 = Option.zip(listOf(o1, o2, t1, t2)) { it.sum() }
    val mz2 = Option.zip({ it.sum() }, o1, t1)
    val mz3 = o2.zipWith(t2) { a, b -> a + b }
    val mz4 = o1.zipWith(t2) { a, b -> a + b }
    val mz5 = o1.zipWith(t1) { _, _ -> throw Exception("") }
    val mz6 = o1.zipWith(t1) { a, b -> a + b }
    val mz7 = o1.zipWithNullable(1) { a, b -> a + b }
    val tz1 = Try.zip(listOf(t1, t2, o1, o2)) { it.sum() }
    val tz2 = Try.zip({ it.sum() }, t1, o1)
    val tz3 = t1.zipWith(o2) { a, b -> a + b }
    val tz4 = t1.zipWith(o1) { _, _ -> throw Exception("") }
    val tz5 = t1.zipWithNullable(null) { _, _ -> 1 }

    /// Then
    Assert.assertTrue(mz1.isNothing)
    Assert.assertEquals(mz2.value, 2)
    Assert.assertTrue(mz3.isNothing)
    Assert.assertTrue(mz4.isNothing)
    Assert.assertTrue(mz5.isNothing)
    Assert.assertEquals(mz6.value, 2)
    Assert.assertEquals(mz7.value, 2)
    Assert.assertTrue(tz1.isFailure)
    Assert.assertEquals(tz2.value, 2)
    Assert.assertTrue(tz3.isFailure)
    Assert.assertTrue(tz4.isFailure)
    Assert.assertTrue(tz5.isFailure)
  }

  @Test
  fun test_filter_shouldWork() {
    /// Setup
    val o1 = Option.some(1)
    val o2 = Option.nothing<Int>()
    val t1 = Try.success(1)
    val t2 = Try.failure<Int>("Error")

    /// When
    val o1f = o1.filter { it % 2 == 0 }
    val o2f = o2.filter { it % 2 != 0 }
    val o3f = o1.filter { it % 2 != 0 }
    val o4f = o1.filter { throw Exception("") }
    val t1f = t1.filter({ it % 2 == 0 }, "Error 2")
    val t2f = t2.filter({ it % 2 == 0 }, "Error 2")
    val t3f = t1.filter({ throw Exception("Error 3") }, "Error 2")

    /// Then
    Assert.assertTrue(o1f.isNothing)
    Assert.assertTrue(o2f.isNothing)
    Assert.assertEquals(o3f.value, 1)
    Assert.assertTrue(o4f.isNothing)
    Assert.assertEquals(t1f.error?.message, "Error 2")
    Assert.assertEquals(t2f.error?.message, "Error")
    Assert.assertEquals(t3f.error?.message, "Error 3")
  }

  @Test
  fun test_doOnNextOrOnError_shouldWork() {
    /// Setup
    var valueCount = 0
    var errorCount = 0
    val o1 = Option.some(1)
    val o2 = Option.nothing<Int>()
    val t1 = Try.success(1)
    val t2 = Try.failure<Int>()

    /// When
    o1.doOnNext { valueCount += it }
    o1.doOnNothing { errorCount += 1 }
    o2.doOnNext { valueCount += it }
    o2.doOnNothing { errorCount += 1 }
    t1.doOnNext { valueCount += it }
    t1.doOnError { errorCount += 1 }
    t2.doOnNext { valueCount += it }
    t2.doOnError { errorCount += 1 }

    /// Then
    Assert.assertEquals(valueCount, 2)
    Assert.assertEquals(errorCount, 2)
  }
}