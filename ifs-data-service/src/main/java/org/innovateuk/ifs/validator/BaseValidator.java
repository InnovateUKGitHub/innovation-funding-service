package org.innovateuk.ifs.validator;

import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.springframework.validation.Validator;

/**
 * This class can be user to share the same interface on multiple form validator classes.
 */
public abstract class BaseValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return FormInputResponse.class.equals(clazz);
    }
}
