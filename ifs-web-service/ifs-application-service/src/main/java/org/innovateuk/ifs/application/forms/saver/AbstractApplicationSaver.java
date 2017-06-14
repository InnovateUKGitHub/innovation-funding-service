package org.innovateuk.ifs.application.forms.saver;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.ValidationMessages;

import java.util.List;


import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilterNot;


/**
 * Abstract application saver for Question and Section
 */
abstract class AbstractApplicationSaver {

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
}
