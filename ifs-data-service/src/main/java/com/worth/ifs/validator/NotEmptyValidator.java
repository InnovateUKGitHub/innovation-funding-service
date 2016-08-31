package com.worth.ifs.validator;

import com.worth.ifs.form.domain.FormInputResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import static com.worth.ifs.commons.rest.ValidationMessages.rejectValue;

/**
 * This class validates the FormInputResponse, it checks if there is a value present.
 */
@Component
public class NotEmptyValidator extends BaseValidator {
    private static final Log LOG = LogFactory.getLog(NotEmptyValidator.class);


    @Override
    public void validate(Object target, Errors errors) {
        LOG.debug("do NotEmpty validation ");
        FormInputResponse response = (FormInputResponse) target;

        if (StringUtils.isEmpty(response.getValue()) || "".equals(response.getValue().trim())) {
            LOG.debug("NotEmpty validation message for: " + response.getId());
            rejectValue(errors, "value", "validation.field.please.enter.some.text");
        }
    }
}
