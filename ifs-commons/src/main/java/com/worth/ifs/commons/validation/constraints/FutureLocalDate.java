package com.worth.ifs.commons.validation.constraints;

import com.worth.ifs.commons.validation.FutureLocalDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validation annotation to assert that a required string contains less than or equal to a maximum number of allowed words.
 */
@Documented
@Constraint(validatedBy = FutureLocalDateValidator.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface FutureLocalDate {

    String message() default "{com.worth.ifs.validator.constraints.WordCount.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}