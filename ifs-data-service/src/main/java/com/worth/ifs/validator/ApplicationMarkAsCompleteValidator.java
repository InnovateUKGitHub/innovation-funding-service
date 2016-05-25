package com.worth.ifs.validator;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.form.domain.FormInputResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

/**
 * Validates the inputs in the application details, if valid on the markAsComplete action
 *
 */
public class ApplicationMarkAsCompleteValidator extends BaseValidator{
    private static final Log LOG = LogFactory.getLog(ApplicationMarkAsCompleteValidator.class);

    @Override
    public void validate(Object target, Errors errors) {
        LOG.debug("do ApplicationMarkAsComplete Validation");

        FormInputResponse response = (FormInputResponse) target;
        Application application = response.getApplication();

        if (StringUtils.isEmpty(application.getName())) {
            LOG.debug("MarkAsComplete validation message for: " + application.getName());
            errors.rejectValue("value", "response.emptyResponse", "Please enter the full title of the project");
        }

        if (StringUtils.isEmpty(application.getDurationInMonths()) || application.getDurationInMonths() < 0 || application.getDurationInMonths() > 99) {
            LOG.debug("MarkAsComplete validation message for: " + application.getDurationInMonths());
            errors.rejectValue("value", "response.emptyResponse", "Please enter a valid duration");
        }

        if (StringUtils.isEmpty(application.getStartDate())) {
           LOG.debug("MarkAsComplete validation message for: " + response.getId());
         //   errors.rejectValue("value", "response.emptyResponse", "Please enter some text");
        }

    }
}
