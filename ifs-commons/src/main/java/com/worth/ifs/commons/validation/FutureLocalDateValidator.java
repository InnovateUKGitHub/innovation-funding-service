package com.worth.ifs.commons.validation;

import com.worth.ifs.commons.validation.constraints.FutureLocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

/**
 * A validator that asserts that a required string contains less than or equal to a maximum number of allowed words.
 */
public class FutureLocalDateValidator implements ConstraintValidator<FutureLocalDate, LocalDate> {

    private FutureLocalDate constraintAnnotation;

    @Override
    public void initialize(FutureLocalDate constraintAnnotation) {
        this.constraintAnnotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value.isAfter(LocalDate.now());
    }
}