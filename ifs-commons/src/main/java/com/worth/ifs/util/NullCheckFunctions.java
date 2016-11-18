package com.worth.ifs.util;

import java.util.stream.Stream;

/**
 * utility class to assist in null checks.
 */
public final class NullCheckFunctions {
	private NullCheckFunctions(){}
	
	/**
	 * returns true iff all arguments are null.
	 */
	public static boolean allNull(Object...objects) {
		return Stream.of(objects).allMatch(o -> o == null);
	}
}
