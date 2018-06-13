package org.innovateuk.ifs.async.annotations;

import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;

import java.lang.annotation.*;

/**
 * Allows a method (and any methods that it uses) to use the Async futures mechanism via
 * {@link AsyncFuturesGenerator}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsyncMethod {
}
