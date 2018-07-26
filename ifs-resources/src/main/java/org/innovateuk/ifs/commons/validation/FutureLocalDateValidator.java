package org.innovateuk.ifs.commons.validation;

import org.innovateuk.ifs.commons.validation.constraints.FutureLocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

/**
 * A validator that asserts that a LocalDate value is in the future.
 */
public class FutureLocalDateValidator implements ConstraintValidator<FutureLocalDate, LocalDate> {


    @Override
    public void initialize(FutureLocalDate constraintAnnotation) {
        // does nothing
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if(value == null) {
            return false;
        }

        LocalDate today = LocalDate.now();

        return value.isAfter(today);
    }
}
