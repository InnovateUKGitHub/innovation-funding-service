package com.worth.ifs.util;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;


public class PairFunctionsTest {

    @Test
    public void testLeftPair() {
        Assert.assertEquals("hello", PairFunctions.leftPair().apply(Pair.of("hello", true)));
    }

    @Test
    public void testPresentRightPair() {
        Assert.assertEquals("there", PairFunctions.presentRightPair().apply(Pair.of("hello", Optional.of("there"))));
    }

    @Test
    public void testRightPairIsPresent() {
        Assert.assertEquals(true, PairFunctions.rightPairIsPresent().test(Pair.of("hello", Optional.of("there"))));
        Assert.assertEquals(false, PairFunctions.rightPairIsPresent().test(Pair.of("hello", Optional.empty())));
    }

}

