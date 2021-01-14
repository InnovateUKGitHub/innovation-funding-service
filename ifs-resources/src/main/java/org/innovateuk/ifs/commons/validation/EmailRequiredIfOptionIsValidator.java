package org.innovateuk.ifs.commons.validation;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.validation.constraints.EmailRequiredIfOptionIs;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;

/**
 * A validator that asserts that a required email is not {@code null} or blank or invalid if a separate predicate is met.
 */
public class EmailRequiredIfOptionIsValidator implements ConstraintValidator<EmailRequiredIfOptionIs, Object> {
    private EmailRequiredIfOptionIs fieldRequiredIf;

    private static final Log LOG = LogFactory.getLog(FieldRequiredIfOptionIsValidator.class);

    @Override
    public void initialize(EmailRequiredIfOptionIs fieldRequiredIf) {
        this.fieldRequiredIf = fieldRequiredIf;
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        try {
            Object requiredFieldValue = PropertyUtils.getProperty(object, fieldRequiredIf.required());
            if (isPredicateMet(object, fieldRequiredIf.argument(), fieldRequiredIf.predicate())) {
                if (isRequiredFieldBlank(requiredFieldValue, fieldRequiredIf.required())) {
                    addConstraintViolationMessageToField(context, fieldRequiredIf.message(), fieldRequiredIf.required());
                    return false;
                } else if (!isRequiredFieldValid(requiredFieldValue, fieldRequiredIf.required(), fieldRequiredIf.regexp())) {
                    addConstraintViolationMessageToField(context, fieldRequiredIf.invalidMessage(), fieldRequiredIf.required());
                    return false;
                }
            }
            return true;
        } catch (final Exception e) {
            LOG.error("Caught Exception", e);
            return false;
        }
    }

    private boolean isPredicateMet(Object object, String argumentFieldName, long predicateValue) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Long argumentValue = (Long) PropertyUtils.getProperty(object, argumentFieldName);
        return argumentValue != null && argumentValue.equals(predicateValue);
    }

    private boolean isRequiredFieldBlank(Object requiredFieldValue, String requiredFieldName) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (null == requiredFieldValue) {
            return true;
        }
        if (requiredFieldValue instanceof String) {
            return StringUtils.isBlank((String) requiredFieldValue);
        }

        throw new IllegalArgumentException("The required field that must have a non blank value [" + requiredFieldName + "] must be of type String. Found " + requiredFieldValue.getClass().getName());
    }
    private boolean isRequiredFieldValid(Object requiredFieldValue, String requiredFieldName, String regex) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (requiredFieldValue instanceof String) {
            if(Pattern.matches(regex, (String) requiredFieldValue)) {
                EmailValidator externalEmailValidator = new EmailValidator();
                return externalEmailValidator.isValid((String) requiredFieldValue, null);
            } else {
                return false;
            }
        }

        throw new IllegalArgumentException("The required field that must have a non blank value [" + requiredFieldName + "] must be of type String. Found " + requiredFieldValue.getClass().getName());
    }

    private void addConstraintViolationMessageToField(ConstraintValidatorContext context, String message, String fieldName) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addPropertyNode(fieldName).addConstraintViolation();
    }
}
