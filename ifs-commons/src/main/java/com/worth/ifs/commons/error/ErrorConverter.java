package com.worth.ifs.commons.error;

import java.util.Optional;
import java.util.function.Function;

/**
 * Represents a class that can potentially transform an Error into another Error
 */
public interface ErrorConverter extends Function<Error, Optional<Error>> {
}
