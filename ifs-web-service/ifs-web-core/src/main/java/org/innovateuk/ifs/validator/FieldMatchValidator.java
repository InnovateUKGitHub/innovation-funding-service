package org.innovateuk.ifs.validator;

import org.innovateuk.ifs.validator.constraints.FieldMatch;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * A validator that tests two fields to be equal to each other
 */

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private static final Log LOG = LogFactory.getLog(FieldMatchValidator.class);

    private String firstFieldName;
    private String secondFieldName;
    private String message;

    @Override
    public void initialize(final FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        boolean matches = false;
        try {
            BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);
            final Object firstObject = beanWrapper.getPropertyValue(firstFieldName);
            final Object secondObject = beanWrapper.getPropertyValue(secondFieldName);
            matches = objectsMatch(firstObject, secondObject);
        } catch(final Exception ignore) {
            LOG.error(ignore);
        }

        if (!matches) {
            addConstraintViolationMessageToField(context, message, secondFieldName);
        }

        return matches;
    }

    private boolean objectsMatch(Object firstObject, Object secondObject) {
        return firstObject == null && secondObject == null || firstObject != null && firstObject.equals(secondObject);
    }

    private void addConstraintViolationMessageToField(ConstraintValidatorContext context, String message, String fieldName) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(fieldName)
                .addConstraintViolation();
    }
}
