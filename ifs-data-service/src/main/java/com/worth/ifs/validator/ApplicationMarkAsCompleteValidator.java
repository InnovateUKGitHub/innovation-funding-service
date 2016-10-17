package com.worth.ifs.validator;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

import static com.worth.ifs.commons.rest.ValidationMessages.rejectValue;

/**
 * Validates the inputs in the application details, if valid on the markAsComplete action
 *
 */
public class ApplicationMarkAsCompleteValidator implements Validator {
    private static final Log LOG = LogFactory.getLog(ApplicationMarkAsCompleteValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return Application.class.isAssignableFrom(clazz);
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

        if (application.getResubmission() != null) {
            if (application.getResubmission()) {
                if (StringUtils.isEmpty(application.getPreviousApplicationNumber())) {
                    LOG.debug("MarkAsComplete application details validation message for previous application number: " + application.getPreviousApplicationNumber());
                    rejectValue(errors, "previousApplicationNumber", "validation.application.previous.application.number.required");
                }
                if (StringUtils.isEmpty(application.getPreviousApplicationTitle())) {
                    LOG.debug("MarkAsComplete application details validation message for previous application title: " + application.getPreviousApplicationTitle());
                    rejectValue(errors, "previousApplicationTitle", "validation.application.previous.application.title.required");
                }
            }
        } else {
            LOG.debug("MarkAsComplete application details validation message for resubmission indicator: " + application.getResubmission());
            rejectValue(errors, "resubmission", "validation.application.must.indicate.resubmission.or.not");
        }

    }
}
