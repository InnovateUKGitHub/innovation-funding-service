package org.innovateuk.ifs.commons.validation;

import org.innovateuk.ifs.commons.validation.constraints.PastLocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

/**
 * A validator that asserts that a LocalDate value is in the future.
 */
public class PastLocalDateValidator implements ConstraintValidator<PastLocalDate, LocalDate> {


    @Override
    public void initialize(PastLocalDate constraintAnnotation) {
        // does nothing
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if(value == null) {
            return false;
        }

        LocalDate today = LocalDate.now();

        return value.isBefore(today);
    }
}
