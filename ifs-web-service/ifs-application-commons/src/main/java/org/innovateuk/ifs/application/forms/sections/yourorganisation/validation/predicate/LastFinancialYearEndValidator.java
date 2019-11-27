package org.innovateuk.ifs.application.forms.sections.yourorganisation.validation.predicate;

import static java.util.Objects.isNull;


import java.time.YearMonth;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.validation.constraints.LastFinancialYearEnd;

public class LastFinancialYearEndValidator implements ConstraintValidator<LastFinancialYearEnd, YearMonth> {

    private String messageNotNull;
    private String messagePastYearMonth;
    private String messagePositiveYearMonth;

    @Override
    public void initialize(LastFinancialYearEnd constraintAnnotation) {
        messageNotNull = constraintAnnotation.messageNotNull();
        messagePastYearMonth = constraintAnnotation.messagePastYearMonth();
        messagePositiveYearMonth = constraintAnnotation.messagePositiveYearMonth();
    }

    @Override
    public boolean isValid(YearMonth value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        if(isNull(value)) {
            context.buildConstraintViolationWithTemplate(messageNotNull).addConstraintViolation();
            return false;
        } else if(!isPositive(value)){
            context.buildConstraintViolationWithTemplate(messagePositiveYearMonth).addConstraintViolation();
            return false;
        } else if(!isPast(value)) {
            context.buildConstraintViolationWithTemplate(messagePastYearMonth)
                .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean isPositive(YearMonth value){
        return value.getYear() > 0;
    }

    private boolean isPast(YearMonth value){
        YearMonth today = YearMonth.now();
        return value.isBefore(today);
    }
}
