package com.worth.ifs.util;

import java.util.Optional;

/**
 * A utility class providing handy methods around parsing.
 */
public final class ParsingFunctions {

	private ParsingFunctions() {}
	
    public static Optional<Long> validLong(String string) {
        try {
            return Optional.of(Long.parseLong(string));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
