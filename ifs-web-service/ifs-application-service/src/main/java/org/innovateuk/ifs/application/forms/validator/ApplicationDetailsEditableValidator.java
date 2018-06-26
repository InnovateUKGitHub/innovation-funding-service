package org.innovateuk.ifs.application.forms.validator;

import org.innovateuk.ifs.application.service.QuestionService;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.question.resource.QuestionSetupType.APPLICATION_DETAILS;

/**
 * Component that can be used to check if application details can be edited or not.
 */
@Component
public class ApplicationDetailsEditableValidator extends QuestionEditableValidator {

    public ApplicationDetailsEditableValidator(final QuestionService questionService) {
        super(questionService, APPLICATION_DETAILS);
    }
}