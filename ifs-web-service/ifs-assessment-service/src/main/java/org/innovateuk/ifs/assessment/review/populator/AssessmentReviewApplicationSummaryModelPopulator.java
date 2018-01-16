package org.innovateuk.ifs.assessment.review.populator;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.populator.OrganisationDetailsModelPopulator;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

/**
 * Build the model for the Application under review view.
 */
@Component
public class AssessmentReviewApplicationSummaryModelPopulator {

    @Autowired
    private FormInputResponseService formInputResponseService;

    @Autowired
    private FormInputResponseRestService formInputResponseRestService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private ApplicationModelPopulator applicationModelPopulator;

    @Autowired
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;

    public void populateModel(Model model, ApplicationForm form, UserResource user, long applicationId) {
        List<FormInputResponseResource> responses = formInputResponseRestService.getResponsesByApplicationId(applicationId).getSuccessObjectOrThrowException();
        model.addAttribute("responses", formInputResponseService.mapFormInputResponsesToFormInput(responses));

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());

        addApplicationAndSectionsInternalWithOrgDetails(application, competition, user, model, form, userApplicationRoles, Optional.of(Boolean.FALSE));

        form.setAdminMode(true);
        applicationModelPopulator.addOrganisationAndUserFinanceDetails(competition.getId(), applicationId, user, model, form, null);
    }

    private void addApplicationAndSectionsInternalWithOrgDetails(final ApplicationResource application, final CompetitionResource competition, final UserResource user, final Model model, final ApplicationForm form, List<ProcessRoleResource> userApplicationRoles, final Optional<Boolean> markAsCompleteEnabled) {
        addApplicationAndSectionsInternalWithOrgDetails(application, competition, user, Optional.empty(), Optional.empty(), model, form, userApplicationRoles, markAsCompleteEnabled);
    }

    private void addApplicationAndSectionsInternalWithOrgDetails(final ApplicationResource application, final CompetitionResource competition, final UserResource user, Optional<SectionResource> section, Optional<Long> currentQuestionId, final Model model, final ApplicationForm form, List<ProcessRoleResource> userApplicationRoles, final Optional<Boolean> markAsCompleteEnabled) {
        organisationDetailsModelPopulator.populateModel(model, application.getId(), userApplicationRoles);
        applicationModelPopulator.addApplicationAndSections(application, competition, user, section, currentQuestionId, model, form, userApplicationRoles, markAsCompleteEnabled);
    }
}
