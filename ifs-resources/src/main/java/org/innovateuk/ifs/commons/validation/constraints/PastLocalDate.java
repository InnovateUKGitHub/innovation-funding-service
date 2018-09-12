package org.innovateuk.ifs.commons.validation.constraints;

import org.innovateuk.ifs.commons.validation.PastLocalDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validation annotation to assert that a LocalDate is in the future.
 */
@Documented
@Constraint(validatedBy = PastLocalDateValidator.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface PastLocalDate {

    String message() default "{validation.standard.past.mm.yyyy.not.past.format}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
