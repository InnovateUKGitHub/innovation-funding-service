package org.innovateuk.ifs.commons.validation;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.commons.validation.constraints.FieldComparison;
import org.innovateuk.ifs.commons.validation.predicate.BiPredicateProvider;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for the @{FieldComparison} annotation to compare two field with a provided @{BiPredicate}
 */

@SuppressWarnings("unchecked")
@Slf4j
public class FieldComparisonValidator implements ConstraintValidator<FieldComparison, Object> {

    private String firstFieldName;
    private String secondFieldName;
    private String message;
    private BiPredicateProvider predicate;

    @Override
    public void initialize(final FieldComparison constraintAnnotation) {
        firstFieldName = constraintAnnotation.firstField();
        secondFieldName = constraintAnnotation.secondField();
        message = constraintAnnotation.message();
        try {
            predicate = (BiPredicateProvider) constraintAnnotation.predicate().newInstance();
        } catch (ReflectiveOperationException e) {
            log.error("Caught Exception: ", e);
        }
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        boolean valid = isValid(value);

        if (!valid) {
            addConstraintViolationMessageToField(context, message, firstFieldName);
        }

        return valid;
    }

    private boolean isValid(final Object value) {
        try {
            BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);
            final Object firstObject = beanWrapper.getPropertyValue(firstFieldName);
            final Object secondObject = beanWrapper.getPropertyValue(secondFieldName);

            if (firstObject == null
                    || secondObject == null) {
                return true; // Empty fields should be covered by other validation annotations
            }

            return predicate.predicate().test(firstObject, secondObject);
        } catch(final Exception ignore) {
            log.error("isValid failure", ignore);
            return false;
        }
    }

    private void addConstraintViolationMessageToField(ConstraintValidatorContext context,
                                                      String message,
                                                      String fieldName) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(fieldName)
                .addConstraintViolation();
    }
}
