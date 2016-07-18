package com.worth.ifs.util;

/**
 * utility class to assist in null checks.
 */
public final class NullCheckFunctions {
	private NullCheckFunctions(){}
	
	/**
	 * returns true iff all arguments are null.
	 */
	public static boolean allNull(Object...objects) {
		for(Object o: objects) {
			if(o != null) {
				return false;
			}
		}
		return true;
	}
}
