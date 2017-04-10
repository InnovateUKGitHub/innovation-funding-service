package org.innovateuk.ifs.commons.validation.constraints;

import org.innovateuk.ifs.commons.validation.FutureZonedDateTimeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validation annotation to assert that a {@link java.time.ZonedDateTime} is in the future.
 */
@Documented
@Constraint(validatedBy = FutureZonedDateTimeValidator.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface FutureZonedDateTime {

    String message() default "{validation.standard.datetime.future}";

    String timezone() default "Europe/London";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
