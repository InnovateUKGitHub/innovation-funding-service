package org.innovateuk.ifs.application.forms.saver;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.*;


/**
 * Abstract application saver for Question and Section
 */
abstract class AbstractApplicationSaver {

    @Autowired
    private ApplicationQuestionFileSaver fileSaver;

    @Autowired
    private ApplicationQuestionNonFileSaver nonFileSaver;

    protected static final String MARKED_AS_COMPLETE_KEY = "application.validation.MarkAsCompleteFailed";

    protected ValidationMessages sortValidationMessages(ValidationMessages validationMessages) {
        List<Error> markAsCompleteError = simpleFilter(validationMessages.getErrors(), e -> e.getErrorKey().equals(MARKED_AS_COMPLETE_KEY));
        List<Error> allExceptMarkAsCompleteError = simpleFilterNot(validationMessages.getErrors(), e -> e.getErrorKey().equals(MARKED_AS_COMPLETE_KEY));

        List<Error> sortedErrors = combineLists(markAsCompleteError, allExceptMarkAsCompleteError);

        validationMessages.setErrors(sortedErrors);
        return validationMessages;
    }

    protected String getFormInputKey(Long formInputId) {
        return "formInput[" + formInputId + "]";
    }

    protected String getFormCostInputKey(Long formInputId) {
        return "formInput[cost-" + formInputId + "]";
    }

    protected ValidationMessages saveQuestionResponses(HttpServletRequest request, List<QuestionResource> questions, Long userId,
                                                       Long processRoleId, Long applicationId, boolean ignoreEmpty) {
        ValidationMessages errors = new ValidationMessages();

        errors.addAll(nonFileSaver.saveNonFileUploadQuestions(questions, request, userId, applicationId, ignoreEmpty));
        errors.addAll(fileSaver.saveFileUploadQuestionsIfAny(questions, request.getParameterMap(), request, applicationId, processRoleId));

        return errors;
    }
}
