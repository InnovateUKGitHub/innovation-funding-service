package org.innovateuk.ifs.application.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.domain.Application;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.innovateuk.ifs.commons.error.ValidationMessages.rejectValue;

/**
 * Validates the inputs in the application research category, if valid on the markAsComplete action
 */
@Component
public class ApplicationResearchMarkAsCompleteValidator implements Validator {
    private static final Log LOG = LogFactory.getLog(ApplicationResearchMarkAsCompleteValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        //Check subclasses for in case we receive hibernate proxy class.
        return Application.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        LOG.debug("do ApplicationResearchMarkAsComplete Validation");

        Application application = (Application) target;

        if (application.getResearchCategory() == null) {
            LOG.debug("MarkAsComplete application validation message for research category is null");
            rejectValue(errors, "researchCategory", "validation.application.research.category.required");
        }

    }

}
