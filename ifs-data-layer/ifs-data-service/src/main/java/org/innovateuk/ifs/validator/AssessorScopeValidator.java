package org.innovateuk.ifs.validator;

import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.rejectValue;
import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_APPLICATION_IN_SCOPE;

/**
 * This class validates the FormInputResponse, it checks that if the response is for scope then that field is not empty
 *
 */
@Component
public class AssessorScopeValidator extends BaseValidator {

    @Override
    public void validate(Object target, Errors errors) {
        FormInputResponse response = (FormInputResponse) target;

        if (ASSESSOR_APPLICATION_IN_SCOPE == response.getFormInput().getType()) {
            String value = response.getValue();
            if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
                rejectValue(errors, "value", "validation.assessor.scope.invalidScope", response.getFormInput().getId());
            }
        }
    }
}
