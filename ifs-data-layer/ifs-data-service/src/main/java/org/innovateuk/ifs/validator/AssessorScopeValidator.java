package org.innovateuk.ifs.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import static org.innovateuk.ifs.commons.rest.ValidationMessages.rejectValue;
import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_APPLICATION_IN_SCOPE;
import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_SCORE;

/**
 * This class validates the FormInputResponse, it checks that if the reponse is for scope then that field is not empty
 *
 */
@Component
public class AssessorScopeValidator extends BaseValidator {
    private static final Log LOG = LogFactory.getLog(AssessorScopeValidator.class);

    @Override
    public void validate(Object target, Errors errors) {
        FormInputResponse response = (FormInputResponse) target;


        String value = response.getValue();
        if (value.equals("true")) {
            rejectValue(errors, "value", "validation.assessor.scope.true");
        } else if (value.equals("false")) {
            rejectValue(errors, "value", "validation.assessor.scope.false");
        } else {
            rejectValue(errors, "value", "should never execute");
        }
    }
}
