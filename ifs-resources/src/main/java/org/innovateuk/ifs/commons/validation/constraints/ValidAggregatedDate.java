package org.innovateuk.ifs.commons.validation.constraints;

import org.innovateuk.ifs.commons.validation.ValidAggregatedDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.ZonedDateTime;

import static java.lang.annotation.ElementType.TYPE;

/**
 * Validation annotation to assert that the aggregation of day, month and year attributes can be combined into a a valid {@link ZonedDateTime}.
 */
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidAggregatedDateValidator.class)
@Repeatable(ValidAggregatedDateContainer.class)
public @interface ValidAggregatedDate {
    String message() default "{constraints.aggregatedDateIsInvalidZonedDateTime}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String yearField();

    String monthField();

    String dayField();
}
