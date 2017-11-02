package org.innovateuk.ifs.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import static org.innovateuk.ifs.commons.rest.ValidationMessages.rejectValue;

/**
 * This class validates the FormInputResponse, it checks that if the response is for scope then that field is not empty
 *
 */
@Component
public class AssessorScopeValidator extends BaseValidator {
    private static final Log LOG = LogFactory.getLog(AssessorScopeValidator.class);

    @Override
    public void validate(Object target, Errors errors) {
        FormInputResponse response = (FormInputResponse) target;

        if (response.getValue().equals("none")) {
            rejectValue(errors, "value", "validation.assessor.scope.invalidScope", response.getFormInput().getId());
        }
    }
}
