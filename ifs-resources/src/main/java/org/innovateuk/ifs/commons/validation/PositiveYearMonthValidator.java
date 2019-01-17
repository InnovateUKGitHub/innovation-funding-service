package org.innovateuk.ifs.commons.validation;

import org.innovateuk.ifs.commons.validation.constraints.PositiveYearMonth;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.YearMonth;

/**
 * A validator that asserts that a YearMonth's year value is greater than zero.
 */
public class PositiveYearMonthValidator implements ConstraintValidator<PositiveYearMonth, YearMonth> {

    @Override
    public void initialize(PositiveYearMonth constraintAnnotation) {
    }

    @Override
    public boolean isValid(YearMonth value, ConstraintValidatorContext context) {

        if (value == null) {
            return false;
        }

        return value.getYear() > 0;
    }
}
