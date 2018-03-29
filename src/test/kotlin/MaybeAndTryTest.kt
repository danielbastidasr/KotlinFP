
import org.testng.Assert
import org.testng.annotations.Test

/**
 * Created by haipham on 29/3/18.
 */

class MaybeAndTryTest {
  @Test
  fun test_asMaybeAndTry_shouldWork() {
    /// Setup
    val m1 = Maybe.some(1)
    val m2 = Maybe.nothing<Int>()
    val t1 = Try.success(1)
    val t2 = Try.failure<Int>()

    /// When & Then
    Assert.assertEquals(m1, m1.asMaybe())
    Assert.assertEquals(m2, m2.asMaybe())
    Assert.assertEquals(t1, t1.asTry())
    Assert.assertEquals(t2, t2.asTry())
    Assert.assertEquals(m1.asTry().value, 1)
    Assert.assertTrue(m2.asTry().isFailure)
    Assert.assertEquals(t1.asMaybe().value, 1)
    Assert.assertTrue(t2.asMaybe().isNothing)
  }

  @Test
  fun test_isSomeAndIsNothing_shouldWork() {
    /// Setup
    val m1 = Maybe.some(1)
    val m2 = Maybe.nothing<Int>()
    val m3 = Maybe.wrap(1)
    val m4 = Maybe.wrap<Int>(null)
    val t1 = Try.success(1)
    val t2 = Try.failure<Int>("")
    val t3 = Try.wrap(1, "")
    val t4 = Try.wrap<Int>(null, "")

    /// When & Then
    Assert.assertEquals(m1.value, 1)
    Assert.assertNull(m2.value)
    Assert.assertEquals(m3.value, 1)
    Assert.assertNull(m4.value)
    Assert.assertEquals(t1.value, 1)
    Assert.assertNull(t1.error)
    Assert.assertNull(t2.value)
    Assert.assertNotNull(t2.error)
    Assert.assertEquals(t3.value, 1)
    Assert.assertNull(t3.error)
    Assert.assertNull(t4.value)
    Assert.assertNotNull(t4.error)
    Assert.assertTrue(m1.isSome)
    Assert.assertFalse(m1.isNothing)
    Assert.assertTrue(m2.isNothing)
    Assert.assertFalse(m2.isSome)
    Assert.assertTrue(m3.isSome)
    Assert.assertFalse(m3.isNothing)
    Assert.assertTrue(m4.isNothing)
    Assert.assertFalse(m4.isSome)
    Assert.assertTrue(t1.isSuccess)
    Assert.assertFalse(t1.isFailure)
    Assert.assertTrue(t2.isFailure)
    Assert.assertFalse(t2.isSuccess)
    Assert.assertTrue(t3.isSuccess)
    Assert.assertFalse(t3.isFailure)
    Assert.assertTrue(t4.isFailure)
    Assert.assertFalse(t4.isSuccess)
  }

  @Test
  fun test_getOrThrow_shouldWork() {
    /// Setup
    val m1 = Maybe.some(1)
    val m2 = Maybe.nothing<Int>()
    val t1 = Try.success(1)
    val t2 = Try.failure<Int>("Error")

    /// When & Then
    Assert.assertEquals(m1.getOrThrow(), 1)

    try {
      m2.getOrThrow()
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
    val m1 = Maybe.some(1)
    val m2 = Maybe.nothing<Int>()

    /// When & Then
    Assert.assertEquals(m1.getOrElse(2), 1)
    Assert.assertEquals(m2.getOrElse(2), 2)
  }

  @Test
  fun test_someOrElseAndSuccessOrElse_shouldWork() {
    /// Setup
    val m1 = Maybe.some(1)
    val m2 = Maybe.nothing<Int>()
    val t1 = Try.success(1)
    val t2 = Try.failure<Int>("Error")

    /// When & Then
    Assert.assertEquals(m1.someOrElse(m2).value, 1)
    Assert.assertEquals(m1.someOrElse(t2).value, 1)
    Assert.assertEquals(m2.someOrElse(m1).value, 1)
    Assert.assertEquals(m2.someOrElse(t1).value, 1)
    Assert.assertEquals(t1.someOrElse(m2).value, 1)
    Assert.assertEquals(t2.someOrElse(m1).value, 1)
    Assert.assertEquals(t1.successOrElse(t2).value, 1)
    Assert.assertEquals(t2.successOrElse(t1).value, 1)
  }

  @Test
  fun test_map_shouldWork() {
    /// Setup
    val m1 = Maybe.some(1)
    val m2 = Maybe.nothing<Int>()
    val t1 = Try.success(1)
    val t2 = Try.failure<Int>("Error")

    /// When
    val m1m = m1.map(Int::toString)
    val m2m = m2.map(Int::toString)
    val m3m = m1.map(Int::toString).map { it + it }
    val t1m = t1.map(Int::toString)
    val t2m = t2.map(Int::toString)

    /// Then
    Assert.assertEquals(m1m.value, "1")
    Assert.assertTrue(m2m.isNothing)
    Assert.assertEquals(m3m.value, "11")
    Assert.assertEquals(t1m.value, "1")
    Assert.assertTrue(t2m.isFailure)
  }

  @Test
  fun test_flatMap_shouldWork() {
    /// Setup
    val m1 = Maybe.some(1)
    val m2 = Maybe.nothing<Int>()
    val t1 = Try.success(1)
    val t2 = Try.failure<Int>("Error")

    /// When
    val m1fm = m1.flatMapNullable<Int> { null }
    val m2fm = m2.flatMap { Maybe.some(1) }
    val t1fm = t1.flatMapNullable<Int> { null }
    val t2fm = t2.flatMap { Try.success(1) }

    /// Then
    Assert.assertTrue(m1fm.isNothing)
    Assert.assertTrue(m2fm.isNothing)
    Assert.assertTrue(t1fm.isFailure)
    Assert.assertTrue(t2fm.isFailure)
  }

  @Test
  fun test_zipAndZipWith_shouldWork() {
    /// Setup
    val m1 = Maybe.some(1)
    val m2 = Maybe.nothing<Int>()
    val t1 = Try.success(1)
    val t2 = Try.failure<Int>()

    /// When
    val mz1 = Maybe.zip(listOf(m1, m2, t1, t2)) { it.sum() }
    val mz2 = Maybe.zip({ it.sum() }, m1, t1)
    val mz3 = m2.zipWith(t2) { a, b -> a + b }
    val mz4 = m1.zipWith(t2) { a, b -> a + b }
    val mz5 = m1.zipWith(t1) { _, _ -> throw Exception("") }
    val mz6 = m1.zipWith(t1) { a, b -> a + b }
    val mz7 = m1.zipWithNullable(1) { a, b -> a + b }
    val tz1 = Try.zip(listOf(t1, t2, m1, m2)) { it.sum() }
    val tz2 = Try.zip({ it.sum() }, t1, m1)
    val tz3 = t1.zipWith(m2) { a, b -> a + b }
    val tz4 = t1.zipWith(m1) { _, _ -> throw Exception("") }
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
    val m1 = Maybe.some(1)
    val m2 = Maybe.nothing<Int>()
    val t1 = Try.success(1)
    val t2 = Try.failure<Int>("Error")

    /// When
    val m1f = m1.filter { it % 2 == 0 }
    val m2f = m2.filter { it % 2 != 0 }
    val m3f = m1.filter { it % 2 != 0 }
    val m4f = m1.filter { throw Exception("") }
    val t1f = t1.filter({ it % 2 == 0 }, "Error 2")
    val t2f = t2.filter({ it % 2 == 0 }, "Error 2")
    val t3f = t1.filter({ throw Exception("Error 3") }, "Error 2")

    /// Then
    Assert.assertTrue(m1f.isNothing)
    Assert.assertTrue(m2f.isNothing)
    Assert.assertEquals(m3f.value, 1)
    Assert.assertTrue(m4f.isNothing)
    Assert.assertEquals(t1f.error?.message, "Error 2")
    Assert.assertEquals(t2f.error?.message, "Error")
    Assert.assertEquals(t3f.error?.message, "Error 3")
  }
}