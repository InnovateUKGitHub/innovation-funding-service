package org.innovateuk.ifs.application.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import static org.innovateuk.ifs.commons.error.ValidationMessages.rejectValue;

/**
 * This class validates the FormInputResponse, it checks if there is a value present.
 */
@Component
public class RequiredFileValidator extends BaseValidator {
    private static final Log LOG = LogFactory.getLog(RequiredFileValidator.class);

    @Override
    public void validate(Object target, Errors errors) {
        LOG.debug("do NotEmpty validation ");
        FormInputResponse response = (FormInputResponse) target;

        if (response.getFileEntry() == null) {
            rejectValue(errors, "fileEntry", "validation.file.required");
        }
    }
}
