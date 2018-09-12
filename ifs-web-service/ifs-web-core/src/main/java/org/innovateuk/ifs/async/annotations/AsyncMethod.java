package org.innovateuk.ifs.async.annotations;

import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows a method (and any methods that it uses) to use the Async futures mechanism via
 * {@link AsyncFuturesGenerator}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsyncMethod {
}
