package org.innovateuk.ifs.commons.validation;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.validation.constraints.FieldLarger;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * A validator that tests two fields to be equal to each other
 */

public class FieldLargerValidator implements ConstraintValidator<FieldLarger, Object> {
    private static final Log LOG = LogFactory.getLog(FieldLargerValidator.class);

    private String firstFieldName;
    private String secondFieldName;
    private String message;

    @Override
    public void initialize(final FieldLarger constraintAnnotation) {
        firstFieldName = constraintAnnotation.firstField();
        secondFieldName = constraintAnnotation.secondField();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        boolean valid = isValid(value);

        if (!valid) {
            addConstraintViolationMessageToField(context, message, secondFieldName);
        }

        return valid;
    }

    private boolean isValid(final Object value) {
        try {
            final Object firstObject = PropertyUtils.getProperty(value, firstFieldName);
            final Object secondObject = PropertyUtils.getProperty(value, secondFieldName);

            return firstFieldIsLargerThanSecondField(firstObject, secondObject);
        } catch(final Exception ignore) {
            LOG.error(ignore);

            return false;
        }
    }

    private boolean firstFieldIsLargerThanSecondField(Object firstObject,
                                                      Object secondObject) {
        if(firstObject == null
                || secondObject == null) {
            return true; //Empty fields should be covered by other validation annotations
        }

        if(firstObject instanceof Integer
                && secondObject instanceof Integer) {
            return (Integer) firstObject > (Integer) secondObject;
        }

        return false;
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
