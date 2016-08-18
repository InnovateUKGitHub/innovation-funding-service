package com.worth.ifs.validator.constraints;

import com.worth.ifs.validator.PostcodeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validation annotation to validate the postcode as a UK postcode.
 *
 * Example:
 * @Postcode(message = "The password fields must match")
 *
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = PostcodeValidator.class)
@Documented
public @interface Postcode {
    String message() default "{constraints.postcode}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
