package org.innovateuk.ifs.commons;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Used to indicate that a class is a candidate for extraction to an imported library
 * or in some cases replaced with a version from an existing library.
 */
@Retention(SOURCE)
@Target(value = {TYPE})
public @interface LibraryCandidate {
}
