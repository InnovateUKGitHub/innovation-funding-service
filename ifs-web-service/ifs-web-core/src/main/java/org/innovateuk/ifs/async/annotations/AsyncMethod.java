package org.innovateuk.ifs.async.annotations;

import java.lang.annotation.*;

/**
 * Allows a method (and any methods that it uses) to use the Async futures mechanism via
 * {@link org.innovateuk.ifs.async.generation.AsyncFuturesGenerator}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsyncMethod {
}
