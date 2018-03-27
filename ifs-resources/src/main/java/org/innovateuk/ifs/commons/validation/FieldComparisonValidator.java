package org.innovateuk.ifs.commons.validation;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.validation.constraints.FieldComparison;
import org.innovateuk.ifs.commons.validation.predicate.BiPredicateProvider;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for the @{FieldComparison} annotation to compare two field with a provided @{BiPredicate}
 */

public class FieldComparisonValidator implements ConstraintValidator<FieldComparison, Object> {
    private static final Log LOG = LogFactory.getLog(FieldComparisonValidator.class);

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
            LOG.error("Caught Exception: ", e);
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
            final Object firstObject = PropertyUtils.getProperty(value, firstFieldName);
            final Object secondObject = PropertyUtils.getProperty(value, secondFieldName);

            if (firstObject == null
                    || secondObject == null) {
                return true; // Empty fields should be covered by other validation annotations
            }

            return predicate.predicate().test(firstObject, secondObject);
        } catch(final Exception ignore) {
            LOG.error(ignore);

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
