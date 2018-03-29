
import org.testng.Assert
import org.testng.annotations.Test

/**
 * Created by haipham on 29/3/18.
 */

class MaybeAndTryTest {
  @Test
  fun test_asMaybeAndTry_shouldWork() {
    /// Setup
    val o1 = Maybe.some(1)
    val o2 = Maybe.nothing<Int>()
    val t1 = Try.success(1)
    val t2 = Try.failure<Int>("")

    /// When & Then
    Assert.assertEquals(o1, o1.asMaybe())
    Assert.assertEquals(o2, o2.asMaybe())
    Assert.assertEquals(t1, t1.asTry())
    Assert.assertEquals(t2, t2.asTry())
    Assert.assertEquals(o1.asTry().value, 1)
    Assert.assertTrue(o2.asTry().isFailure)
    Assert.assertEquals(t1.asMaybe().value, 1)
    Assert.assertTrue(t2.asMaybe().isNothing)
  }

  @Test
  fun test_isSomeAndIsNothing_shouldWork() {
    /// Setup
    val o1 = Maybe.some(1)
    val o2 = Maybe.nothing<Int>()
    val o3 = Maybe.wrap(1)
    val o4 = Maybe.wrap<Int>(null)
    val t1 = Try.success(1)
    val t2 = Try.failure<Int>("")
    val t3 = Try.wrap(1, "")
    val t4 = Try.wrap<Int>(null, "")

    /// When & Then
    Assert.assertEquals(o1.value, 1)
    Assert.assertNull(o2.value)
    Assert.assertEquals(o3.value, 1)
    Assert.assertNull(o4.value)
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
  }

  @Test
  fun test_getOrThrow_shouldWork() {
    /// Setup
    val o1 = Maybe.some(1)
    val o2 = Maybe.nothing<Int>()
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
    val o1 = Maybe.some(1)
    val o2 = Maybe.nothing<Int>()

    /// When & Then
    Assert.assertEquals(o1.getOrElse(2), 1)
    Assert.assertEquals(o2.getOrElse(2), 2)
  }

  @Test
  fun test_someOrElseAndSuccessOrElse_shouldWork() {
    /// Setup
    val o1 = Maybe.some(1)
    val o2 = Maybe.nothing<Int>()
    val t1 = Try.success(1)
    val t2 = Try.failure<Int>("Error")

    /// When & Then
    Assert.assertEquals(o1.someOrElse(o2).value, 1)
    Assert.assertEquals(o1.someOrElse(t2).value, 1)
    Assert.assertEquals(o2.someOrElse(o1).value, 1)
    Assert.assertEquals(o2.someOrElse(t1).value, 1)
    Assert.assertEquals(t1.someOrElse(o2).value, 1)
    Assert.assertEquals(t2.someOrElse(o1).value, 1)
    Assert.assertEquals(t1.successOrElse(t2).value, 1)
    Assert.assertEquals(t2.successOrElse(t1).value, 1)
  }

  @Test
  fun test_map_shouldWork() {
    /// Setup
    val o1 = Maybe.some(1)
    val o2 = Maybe.nothing<Int>()
    val t1 = Try.success(1)
    val t2 = Try.failure<Int>("Error")

    /// When
    val o1m = o1.map(Int::toString)
    val o2m = o2.map(Int::toString)
    val t1m = t1.map(Int::toString)
    val t2m = t2.map(Int::toString)

    /// Then
    Assert.assertEquals(o1m.value, "1")
    Assert.assertTrue(o2m.isNothing)
    Assert.assertEquals(t1m.value, "1")
    Assert.assertTrue(t2m.isFailure)
  }

  @Test
  fun test_flatMap_shouldWork() {
    /// Setup
    val o1 = Maybe.some(1)
    val o2 = Maybe.nothing<Int>()
    val t1 = Try.success(1)
    val t2 = Try.failure<Int>("Error")

    /// When
    val o1fm = o1.flatMapNullable<Int> { null }
    val o2fm = o2.flatMap { Maybe.some(1) }
    val t1fm = t1.flatMapNullable<Int> { null }
    val t2fm = t2.flatMap { Try.success(1) }

    /// Then
    Assert.assertTrue(o1fm.isNothing)
    Assert.assertTrue(o2fm.isNothing)
    Assert.assertTrue(t1fm.isFailure)
    Assert.assertTrue(t2fm.isFailure)
  }
}