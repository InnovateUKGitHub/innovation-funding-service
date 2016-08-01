package com.worth.ifs.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NullCheckFunctionsTest {

	@Test
	public void testAllNullAllNull() {
		assertTrue(NullCheckFunctions.allNull(null, null, null, null));
	}
	
	@Test
	public void testAllNullNotAllNull() {
		assertFalse(NullCheckFunctions.allNull(null, null, "hello", null));
	}
}
