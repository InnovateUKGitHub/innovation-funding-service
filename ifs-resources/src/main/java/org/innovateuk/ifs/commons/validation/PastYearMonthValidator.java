package org.innovateuk.ifs.commons.validation;

import org.innovateuk.ifs.commons.validation.constraints.PastYearMonth;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.YearMonth;

/**
 * A validator that asserts that a YearMonth value is in the past and is valid.
 */
public class PastYearMonthValidator implements ConstraintValidator<PastYearMonth, YearMonth> {

    @Override
    public void initialize(PastYearMonth constraintAnnotation) {
    }

    @Override
    public boolean isValid(YearMonth value, ConstraintValidatorContext context) {

        if (value == null) {
            return false;
        }

        YearMonth today = YearMonth.now();

        return value.isBefore(today);
    }
}
