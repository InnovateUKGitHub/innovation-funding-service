package com.worth.ifs.validator;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.form.domain.FormInputResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

/**
 * Validates the inputs in the application details, if valid on the markAsComplete action
 *
 */
public class ApplicationMarkAsCompleteValidator implements Validator {
    private static final Log LOG = LogFactory.getLog(ApplicationMarkAsCompleteValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return Application.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LocalDate currentDate = LocalDate.now();

        LOG.debug("do ApplicationMarkAsComplete Validation");

        Application application = (Application) target;

        if (StringUtils.isEmpty(application.getName())) {
            LOG.debug("MarkAsComplete application details validation message for name: " + application.getName());
            rejectValue(errors, "name", "validation.project.name.must.not.be.empty");
        }

        if (StringUtils.isEmpty(application.getStartDate()) || (application.getStartDate().isBefore(currentDate))) {
            LOG.debug("MarkAsComplete application details validation message for start date: " + application.getStartDate());
            rejectValue(errors, "startDate", "validation.project.start.date.not.in.future");
        }

        if (StringUtils.isEmpty(application.getDurationInMonths()) || application.getDurationInMonths() < 1 || application.getDurationInMonths() > 36) {
            LOG.debug("MarkAsComplete application details validation message for duration in months: " + application.getDurationInMonths());
            rejectValue(errors, "durationInMonths", "validation.project.duration.range.invalid");
        }
    }
}
