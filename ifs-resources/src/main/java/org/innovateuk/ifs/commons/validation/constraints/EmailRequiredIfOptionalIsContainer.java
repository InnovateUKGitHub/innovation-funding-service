package org.innovateuk.ifs.commons.validation.constraints;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * Containing annotation type for the {@link EmailRequiredIfOptionIs} annotation.
 */
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailRequiredIfOptionalIsContainer {
    EmailRequiredIfOptionIs[] value();
}
