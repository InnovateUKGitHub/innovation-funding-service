package org.innovateuk.ifs.application.validator;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.application.domain.Application;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.innovateuk.ifs.commons.error.ValidationMessages.rejectValue;

/**
 * Validates the inputs in the application research category, if valid on the markAsComplete action
 */
@Slf4j
@Component
public class ApplicationResearchMarkAsCompleteValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        //Check subclasses for in case we receive hibernate proxy class.
        return Application.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        log.debug("do ApplicationResearchMarkAsComplete Validation");

        Application application = (Application) target;

        if (application.getResearchCategory() == null) {
            log.debug("MarkAsComplete application validation message for research category is null");
            rejectValue(errors, "researchCategory", "validation.application.research.category.required");
        }

    }

}
