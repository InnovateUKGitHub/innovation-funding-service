package org.innovateuk.ifs.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
