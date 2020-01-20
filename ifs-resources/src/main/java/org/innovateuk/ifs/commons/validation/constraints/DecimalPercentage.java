package org.innovateuk.ifs.commons.validation.constraints;

import org.innovateuk.ifs.commons.validation.DecimalPercentageValidator;

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
@Constraint(validatedBy = DecimalPercentageValidator.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface DecimalPercentage {

    String message() default "{validation.finance.percentage}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
