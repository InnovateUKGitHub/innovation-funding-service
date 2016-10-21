package com.worth.ifs.commons.validation;

import com.worth.ifs.commons.validation.constraints.FieldRequiredIf;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * A validator that asserts that a required string is not {@code null} or blank if a separate predicate is met.
 */
public class FieldRequiredIfValidator implements ConstraintValidator<FieldRequiredIf, Object> {

    private FieldRequiredIf fieldRequiredIf;

    private static final Log LOG = LogFactory.getLog(FieldRequiredIfValidator.class);

    @Override
    public void initialize(FieldRequiredIf fieldRequiredIf) {
        this.fieldRequiredIf = fieldRequiredIf;
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        boolean valid = isValid(object);

        if (!valid) {
            addConstraintViolationMessageToField(context, fieldRequiredIf.message(), fieldRequiredIf.required());
        }

        return valid;
    }

    private boolean isValid(Object object) {
        try {
            if (isPredicateMet(object, fieldRequiredIf.argument(), fieldRequiredIf.predicate())) {
                return !isRequiredFieldBlank(object, fieldRequiredIf.required());
            }
            return true;
        } catch (final Exception e) {
            LOG.error("Caught Exception", e);
            return false;
        }
    }

    private boolean isPredicateMet(Object object, String argumentFieldName, boolean predicateValue) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Boolean argumentValue = (Boolean) PropertyUtils.getProperty(object, argumentFieldName);
        return argumentValue != null && argumentValue == predicateValue;
    }

    private boolean isRequiredFieldBlank(Object object, String requiredFieldName) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Object requiredFieldValue = PropertyUtils.getProperty(object, requiredFieldName);

        if (requiredFieldValue instanceof String) {
            return isRequiredFieldValueBlank((String) requiredFieldValue);
        }

        if (requiredFieldValue instanceof Collection) {
            return isRequiredFieldValueBlank((Collection) requiredFieldValue);
        }

        throw new IllegalArgumentException("The required field that must have a non blank value [" + requiredFieldName + "] must be of type String or Collection. Found " + requiredFieldValue.getClass().getName());
    }

    private boolean isRequiredFieldValueBlank(String requiredFieldValue) {
        return StringUtils.isBlank(requiredFieldValue);
    }

    private boolean isRequiredFieldValueBlank(Collection<?> requiredFieldValue) {
        return requiredFieldValue.isEmpty();
    }

    private void addConstraintViolationMessageToField(ConstraintValidatorContext context, String message, String fieldName) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addPropertyNode(fieldName).addConstraintViolation();
    }
}