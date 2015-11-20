package com.worth.ifs.validator;

import com.worth.ifs.form.domain.FormInputResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

/**
 * This class validates the FormInputResponse, it checks if there is a value present.
 */
@Component
public class NotEmptyValidator extends BaseValidator {
    private final Log log = LogFactory.getLog(getClass());


    @Override
    public void validate(Object target, Errors errors) {
        log.debug("do NotEmpty validation ");
        FormInputResponse response = (FormInputResponse) target;

        if (StringUtils.isEmpty(response.getValue()) || response.getValue().trim() == "") {
            log.debug("NotEmpty validation message for: " + response.getId());
            errors.rejectValue("value", "response.emptyResponse", "Please enter some text");
        }
    }
}
