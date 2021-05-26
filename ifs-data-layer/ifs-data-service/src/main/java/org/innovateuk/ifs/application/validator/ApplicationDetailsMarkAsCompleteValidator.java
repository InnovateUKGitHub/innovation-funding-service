package org.innovateuk.ifs.application.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.competition.domain.Competition;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

import static org.innovateuk.ifs.commons.error.ValidationMessages.rejectValue;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.PROCUREMENT;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * Validates the inputs in the application details, if valid on the markAsComplete action
 */
@Component
public class ApplicationDetailsMarkAsCompleteValidator implements Validator {
    private static final Log LOG = LogFactory.getLog(ApplicationDetailsMarkAsCompleteValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        //Check subclasses for in case we receive hibernate proxy class.
        return Application.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LOG.debug("do ApplicationDetailsMarkAsComplete Validation");

        Application application = (Application) target;

        validateApplication(errors, application);
    }

    private void validateApplication(Errors errors,Application application) {
        validateName(errors, application);
        validateStateDate(errors, application);
        validateProjectDuration(application, errors);

        Competition competition = application.getCompetition();
        if (competition.getFundingType() == PROCUREMENT) {
            validateProcurement(errors, application);
        }

        validateInnovationArea(errors, application);
        validateResubmission(errors, application, competition);
    }

    private void validateResubmission(Errors errors, Application application, Competition competition) {
        if (competition.getResubmission() && application.getResubmission() == null) {
            LOG.debug("MarkAsComplete application details validation message for resubmission indicator: " + application.getResubmission());
            rejectValue(errors, "resubmission", "validation.application.must.indicate.resubmission.or.not");
        }

        if (application.getResubmission() != null) {
            if (application.getResubmission()) {
                if (isEmpty(application.getPreviousApplicationNumber())) {
                    LOG.debug("MarkAsComplete application details validation message for previous application number: " + application.getPreviousApplicationNumber());
                    rejectValue(errors, "previousApplicationNumber", "validation.application.previous.application.number.required");
                }
                if (isEmpty(application.getPreviousApplicationTitle())) {
                    LOG.debug("MarkAsComplete application details validation message for previous application title: " + application.getPreviousApplicationTitle());
                    rejectValue(errors, "previousApplicationTitle", "validation.application.previous.application.title.required");
                }
            }
        }
    }

    private void validateInnovationArea(Errors errors, Application application) {
        if (!applicationInnovationAreaIsInCorrectState(application)) {
            LOG.debug("MarkAsComplete application details validation message for innovation area: " + application.getInnovationArea());
            rejectValue(errors, "innovationArea", "validation.application.innovationarea.category.required");
        }
    }

    private void validateStateDate(Errors errors, Application application) {

        LocalDate currentDate = LocalDate.now();

        if (isEmpty(application.getStartDate()) || (application.getStartDate().isBefore(currentDate))) {
            if (!(application.getCompetition().isAlwaysOpen() && application.getCompetition().isKtp())) {
                LOG.debug("MarkAsComplete application details validation message for start date: " + application.getStartDate());
                rejectValue(errors, "startDate", "validation.project.start.date.not.in.future");
            }
        }
    }

    private void validateName(Errors errors, Application application) {
        if (isEmpty(application.getName())) {
            LOG.debug("MarkAsComplete application details validation message for name: " + application.getName());
            rejectValue(errors, "name", "validation.project.name.must.not.be.empty");
        }
    }

    private void validateProcurement(Errors errors, Application application) {
        if (isEmpty(application.getCompetitionReferralSource())) {
            LOG.debug("MarkAsComplete application details validation message for competition Referral Source: " + application.getName());
            rejectValue(errors, "competitionReferralSource", "validation.application.procurement.competitionreferralsource.required");
        }

        if (isEmpty(application.getCompanyAge())) {
            LOG.debug("MarkAsComplete application details validation message for company age: " + application.getName());
            rejectValue(errors, "companyAge", "validation.application.procurement.companyage.required");
        }

        if (isEmpty(application.getCompanyPrimaryFocus())) {
            LOG.debug("MarkAsComplete application details validation message for company primary focus: " + application.getName());
            rejectValue(errors, "companyPrimaryFocus", "validation.application.procurement.companyprimaryfocus.required");
        }
    }


    private void validateProjectDuration(Application application, Errors errors) {
        if (isEmpty(application.getDurationInMonths())) {
            rejectValue(errors, "durationInMonths", "validation.field.must.not.be.blank");
        } else {
            Competition competition = application.getCompetition();
            int maxMonths = competition.getMaxProjectDuration();
            int minMonths = Math.max(application.getMaxMilestoneMonth().orElse(0), competition.getMinProjectDuration());
            boolean minDictatedByCompetition = minMonths == competition.getMinProjectDuration();
            if (application.getDurationInMonths() > maxMonths ||
                    (minDictatedByCompetition && application.getDurationInMonths() < minMonths)) {
                rejectValue(
                        errors,
                        "durationInMonths",
                        "validation.project.duration.input.invalid",
                        minMonths,
                        maxMonths);
            } else if (application.getDurationInMonths() < minMonths) {
                rejectValue(
                        errors,
                        "durationInMonths",
                        "validation.project.duration.must.be.greater.than.milestones");
            }
        }
    }

    private boolean applicationInnovationAreaIsInCorrectState(Application application) {
        return application.getNoInnovationAreaApplicable() == true || (application.getNoInnovationAreaApplicable() == false && application.getInnovationArea() != null);
    }
}
