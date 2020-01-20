package org.innovateuk.ifs.commons.validation;

import org.innovateuk.ifs.commons.validation.constraints.DecimalPercentage;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DecimalPercentageValidator implements ConstraintValidator<DecimalPercentage, BigDecimal> {

    private DecimalPercentage constraintAnnotation;

    @Override
    public void initialize(DecimalPercentage constraintAnnotation) {
        this.constraintAnnotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        Pattern pattern = Pattern.compile("^\\d{1,2}(\\.\\d{1,2})?$");
        Matcher matcher = pattern.matcher(value.toString());
        return matcher.matches();
    }
}
