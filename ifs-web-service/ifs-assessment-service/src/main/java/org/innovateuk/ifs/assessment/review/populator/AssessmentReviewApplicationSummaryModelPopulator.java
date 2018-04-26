package org.innovateuk.ifs.assessment.review.populator;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.populator.OrganisationDetailsModelPopulator;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.user.resource.Role.ASSESSOR;

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
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    private AssessmentRestService assessmentRestService;

    @Autowired
    private ApplicationModelPopulator applicationModelPopulator;

    @Autowired
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;

    public void populateModel(Model model, ApplicationForm form, UserResource user, long applicationId) {
        List<FormInputResponseResource> responses = formInputResponseRestService
                .getResponsesByApplicationId(applicationId).getSuccess();
        model.addAttribute("responses", formInputResponseService.mapFormInputResponsesToFormInput(responses));

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());

        addApplicationAndSectionsInternalWithOrgDetails(application, competition, user, model, form, userApplicationRoles, Optional.of(Boolean.FALSE));

        form.setAdminMode(true);
        applicationModelPopulator.addOrganisationAndUserFinanceDetails(competition.getId(), applicationId, user, model, form, null);

        addAssessmentDetails(userApplicationRoles, user, model, applicationId);
    }

    private void addApplicationAndSectionsInternalWithOrgDetails(ApplicationResource application,
                                                                 CompetitionResource competition,
                                                                 UserResource user,
                                                                 Model model,
                                                                 ApplicationForm form,
                                                                 List<ProcessRoleResource> userApplicationRoles,
                                                                 Optional<Boolean> markAsCompleteEnabled) {

        addApplicationAndSectionsInternalWithOrgDetails(application, competition, user, empty(), empty(), model, form, userApplicationRoles, markAsCompleteEnabled);
    }

    private void addApplicationAndSectionsInternalWithOrgDetails(ApplicationResource application,
                                                                 CompetitionResource competition,
                                                                 UserResource user,
                                                                 Optional<SectionResource> section,
                                                                 Optional<Long> currentQuestionId,
                                                                 Model model,
                                                                 ApplicationForm form,
                                                                 List<ProcessRoleResource> userApplicationRoles,
                                                                 Optional<Boolean> markAsCompleteEnabled) {

        organisationDetailsModelPopulator.populateModel(model, application.getId(), userApplicationRoles);
        applicationModelPopulator.addApplicationAndSections(application, competition, user, section, currentQuestionId, model, form, userApplicationRoles, markAsCompleteEnabled);
    }

    private void addAssessmentDetails(List<ProcessRoleResource> userApplicationRoles,
                                      UserResource user,
                                      Model model,
                                      long applicationId) {

        if (isAssessorForApplication(userApplicationRoles, user)) {

            List<AssessorFormInputResponseResource> allAssessorResponses = assessorFormInputResponseRestService
                    .getAllAssessorFormInputResponsesForPanel(applicationId)
                    .getSuccess();

            if(!allAssessorResponses.isEmpty()) {

                List<AssessmentResource> feedbackSummary = assessmentRestService
                        .getByUserAndApplication(user.getId(), applicationId)
                        .getSuccess();

                long assessmentId = feedbackSummary.get(0).getId();

                List<AssessorFormInputResponseResource> assessorResponses = allAssessorResponses
                        .stream()
                        .filter(response -> response.getAssessment().equals(assessmentId))
                        .collect(toList());

                List<AssessorFormInputResponseResource> questionScore = assessorResponses
                        .stream()
                        .filter(response -> formInputRestService.getById(response.getFormInput())
                                .getSuccess().getDescription().equals("Question score"))
                        .collect(toList());

                List<AssessorFormInputResponseResource> questionFeedback = assessorResponses
                        .stream()
                        .filter(response -> formInputRestService.getById(response.getFormInput())
                                .getSuccess().getDescription().equals("Feedback"))
                        .collect(toList());

                model.addAttribute("feedback", questionFeedback);
                model.addAttribute("score", questionScore);
                model.addAttribute("feedbackSummary", feedbackSummary);
            }
        }
    }

    private boolean isAssessorForApplication(List<ProcessRoleResource> userApplicationRoles, UserResource user) {
        return userApplicationRoles
                .stream()
                .filter(processRoleResource -> processRoleResource.getUser().equals(user.getId()))
                .anyMatch(processRoleResource -> processRoleResource.getRole() == ASSESSOR.getId());
    }
}
