
import maybe.*
import org.testng.Assert
import org.testng.annotations.Test

/**
 * Created by haipham on 29/3/18.
 */

class MaybeTest {
  @Test
  fun test_asMaybe_shouldWork() {
    /// Setup
    val o1 = Maybe.some(1)
    val o2 = Maybe.nothing<Int>()

    /// When & Then
    Assert.assertEquals(o1, o1.asOptional())
    Assert.assertEquals(o2, o2.asOptional())
  }

  @Test
  fun test_isSomeAndIsNothing_shouldWork() {
    /// Setup
    val o1 = Maybe.some(1)
    val o2 = Maybe.nothing<Int>()
    val o3 = Maybe.wrap(1)
    val o4 = Maybe.wrap<Int>(null)

    /// When & Then
    Assert.assertEquals(o1.value, 1)
    Assert.assertNull(o2.value)
    Assert.assertTrue(o1.isSome)
    Assert.assertFalse(o1.isNothing)
    Assert.assertTrue(o2.isNothing)
    Assert.assertFalse(o2.isSome)
    Assert.assertTrue(o3.isSome)
    Assert.assertFalse(o3.isNothing)
    Assert.assertTrue(o4.isNothing)
    Assert.assertFalse(o4.isSome)
  }

  @Test
  fun test_getOrThrow_shouldWork() {
    /// Setup
    val o1 = Maybe.some(1)
    val o2 = Maybe.nothing<Int>()

    /// When & Then
    Assert.assertEquals(o1.getOrThrow(), 1)

    try {
      o2.getOrThrow()
      Assert.fail("Should not have completed")
    } catch (e: Exception) {}
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
  fun test_someOrElse_shouldWork() {
    /// Setup
    val o1 = Maybe.some(1)
    val o2 = Maybe.nothing<Int>()

    /// When & Then
    Assert.assertEquals(o1.someOrElse(o2).value, 1)
    Assert.assertEquals(o2.someOrElse(o1).value, 1)
  }

  @Test
  fun test_map_shouldWork() {
    /// Setup
    val o1 = Maybe.some(1)
    val o2 = Maybe.nothing<Int>()

    /// When
    val o1m = o1.map(Int::toString)
    val o2m = o2.map(Int::toString)

    /// Then
    Assert.assertEquals(o1m.value, "1")
    Assert.assertTrue(o2m.isNothing)
  }

  @Test
  fun test_flatMap_shouldWork() {
    /// Setup
    val o1 = Maybe.some(1)
    val o2 = Maybe.nothing<Int>()

    /// When
    val o1fm = o1.flatMapNullable<Int> { null }
    val o2fm = o2.flatMap { Maybe.some(1) }

    /// Then
    Assert.assertTrue(o1fm.isNothing)
    Assert.assertTrue(o2fm.isNothing)
  }
}