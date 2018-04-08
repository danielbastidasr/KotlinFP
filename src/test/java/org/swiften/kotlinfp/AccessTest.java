package org.swiften.kotlinfp;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by haipham on 31/3/18.
 */
public final class AccessTest {
  @Test
  @SuppressWarnings("ConstantConditions")
  public void test_accessKotlinCode_shouldWork() {
    /// Setup
    Option<Integer> o1 = Option.<Integer>nothing().catchNothing(0);
    Try<Integer> t1 = Try.<Integer>failure("").catchFailure(0);

    /// When & Then
    Assert.assertEquals(o1.getValue().intValue(), 0);
    Assert.assertEquals(t1.getValue().intValue(), 0);
  }
}
