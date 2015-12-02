package com.worth.ifs.validator.util;

import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.domain.FormValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

import java.util.Set;

public class ValidationUtil {
    public final static Log log = LogFactory.getLog(ValidationUtil.class);

    public static BindingResult validateResponse(FormInputResponse response) {
        Set<FormValidator> validators = response.getFormInput().getFormValidators();

        DataBinder binder = new DataBinder(response);

        // Get validators from the FormInput, and add to binder.
        validators.forEach(
                v ->
                {
                    Validator validator = null;
                    try {
                        validator = (Validator) Class.forName(v.getClazzName()).getConstructor().newInstance();
                        binder.addValidators(validator);
                    } catch (Exception e) {
                        log.error("Could not find validator class: " + v.getClazzName());
                        log.error("Exception message: " + e.getMessage());
                    }
                }
        );
        binder.validate();
        BindingResult bindingResult = binder.getBindingResult();
        return bindingResult;
    }


}
