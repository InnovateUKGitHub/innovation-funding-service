package com.worth.ifs.util;

import org.apache.commons.lang3.BooleanUtils;

/**
 * created to fix ambiguous reference compile errors
 */
public final class BooleanFunctions {
	
	private BooleanFunctions(){}
	
    public static boolean and(boolean... booleans){
        return BooleanUtils.and(booleans);
    }

    public static boolean or(boolean... booleans){
        return BooleanUtils.or(booleans);
    }

    public static boolean xor(boolean... booleans){
        return BooleanUtils.xor(booleans);
    }

}
