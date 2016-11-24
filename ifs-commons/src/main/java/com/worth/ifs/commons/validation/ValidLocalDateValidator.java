package com.worth.ifs.commons.validation;

import com.worth.ifs.commons.validation.constraints.ValidLocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

/**
 * TODO: Add description
 */
public class ValidLocalDateValidator implements ConstraintValidator<ValidLocalDate, LocalDate> {

    private ValidLocalDate constraintAnnotation;

    @Override
    public void initialize(ValidLocalDate constraintAnnotation) {
        this.constraintAnnotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return !LocalDate.MIN.equals(value);
    }
}