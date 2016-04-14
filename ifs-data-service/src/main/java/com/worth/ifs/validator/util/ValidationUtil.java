package com.worth.ifs.validator.util;

import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.domain.FormValidator;
import com.worth.ifs.validator.NotEmptyValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

import java.util.Set;

public final class ValidationUtil {
    public final static Log LOG = LogFactory.getLog(ValidationUtil.class);

    private ValidationUtil() {}
    
    public static BindingResult validateResponse(FormInputResponse response, boolean ignoreEmpty) {
        Set<FormValidator> validators = response.getFormInput().getFormValidators();

        DataBinder binder = new DataBinder(response);

        // Get validators from the FormInput, and add to binder.
        validators.forEach(
                v ->
                {
                    Validator validator = null;
                    try {
                        if(!(ignoreEmpty && v.getClazzName().equals(NotEmptyValidator.class.getName()))) {
                            validator = (Validator) Class.forName(v.getClazzName()).getConstructor().newInstance();
                            binder.addValidators(validator);
                        }
                    } catch (Exception e) {
                        LOG.error("Could not find validator class: " + v.getClazzName());
                        LOG.error("Exception message: " + e.getMessage());
                        LOG.error(e);
                    }
                }
        );
        binder.validate();
        return binder.getBindingResult();
    }
}
