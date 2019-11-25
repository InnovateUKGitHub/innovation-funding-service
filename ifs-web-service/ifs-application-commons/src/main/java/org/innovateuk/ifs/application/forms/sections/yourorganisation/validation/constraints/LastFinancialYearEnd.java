package org.innovateuk.ifs.application.forms.sections.yourorganisation.validation.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.validation.predicate.LastFinancialYearEndValidator;

/**
 * Validation annotation to assert that entry is a valid end of last financial year.
 */
@Documented
@Constraint(validatedBy = LastFinancialYearEndValidator.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface LastFinancialYearEnd {

    String message() default "";
    String messageNotNull();
    String messagePastYearMonth();
    String messagePositiveYearMonth();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}