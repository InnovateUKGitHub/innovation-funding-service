package org.innovateuk.ifs.application.forms.saver;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * Abstract application saver for Question and Section
 */
abstract class AbstractApplicationSaver {

    public static final String MARKED_AS_COMPLETE_KEY = "application.validation.MarkAsCompleteFailed";

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

}
