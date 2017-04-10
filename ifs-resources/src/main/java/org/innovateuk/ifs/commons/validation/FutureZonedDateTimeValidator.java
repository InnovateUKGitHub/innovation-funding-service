package org.innovateuk.ifs.commons.validation;

import org.innovateuk.ifs.commons.validation.constraints.FutureZonedDateTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Validator that checks if a {@link ZonedDateTime} is in the future.
 */
public class FutureZonedDateTimeValidator implements ConstraintValidator<FutureZonedDateTime, ZonedDateTime> {

    private FutureZonedDateTime constraint;

    @Override
    public void initialize(FutureZonedDateTime constraintAnnotation) {
        this.constraint = constraintAnnotation;
    }

    @Override
    public boolean isValid(ZonedDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        ZoneId timezone = ZoneId.of(constraint.timezone());
        ZonedDateTime now = ZonedDateTime.now(timezone);

        return value.isAfter(now);
    }
}
