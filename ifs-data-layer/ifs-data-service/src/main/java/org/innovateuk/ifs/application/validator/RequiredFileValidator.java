package org.innovateuk.ifs.application.validator;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import static org.innovateuk.ifs.commons.error.ValidationMessages.rejectValue;

/**
 * This class validates the FormInputResponse, it checks if there is a value present.
 */
@Slf4j
@Component
public class RequiredFileValidator extends BaseValidator {

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("do NotEmpty validation ");
        FormInputResponse response = (FormInputResponse) target;

        if (response.getFileEntries() == null || response.getFileEntries().isEmpty()) {
            rejectValue(errors, "fileEntries", "validation.file.required");
        }
    }
}
