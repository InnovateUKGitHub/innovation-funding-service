package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.populator.OrganisationDetailsModelPopulator;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

/**
 * This controller will handle all requests that are related to the feedback for applicant in an interview.
 * Application interview feedback is the page that allows the applicant
 * to view the feedback early ready for the interview
 */
@Controller
@RequestMapping("/application/{applicationId}")
@SecuredBySpring(value = "Controller", description = "Each applicant has permission to view their application feedback for interview", securedType = ApplicationFeedbackController.class)
@PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationCompositeId', 'APPLICATION_FEEDBACK')")
public class ApplicationFeedbackController {

    private ProcessRoleService processRoleService;
    private ApplicationService applicationService;
    private CompetitionService competitionService;
    private ApplicationModelPopulator applicationModelPopulator;
    private FormInputResponseRestService formInputResponseRestService;
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;
    private UserRestService userRestService;
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;
    private AssessmentRestService assessmentRestService;

    @Autowired
    public ApplicationFeedbackController(ProcessRoleService processRoleService,
                                         ApplicationService applicationService,
                                         CompetitionService competitionService,
                                         ApplicationModelPopulator applicationModelPopulator,
                                         FormInputResponseRestService formInputResponseRestService,
                                         OrganisationDetailsModelPopulator organisationDetailsModelPopulator,
                                         UserRestService userRestService,
                                         AssessorFormInputResponseRestService assessorFormInputResponseRestService,
                                         AssessmentRestService assessmentRestService) {
        this.processRoleService = processRoleService;
        this.applicationService = applicationService;
        this.competitionService = competitionService;
        this.applicationModelPopulator = applicationModelPopulator;
        this.formInputResponseRestService = formInputResponseRestService;
        this.organisationDetailsModelPopulator = organisationDetailsModelPopulator;
        this.userRestService = userRestService;
        this.assessorFormInputResponseRestService = assessorFormInputResponseRestService;
        this.assessmentRestService = assessmentRestService;

    }

    @GetMapping("/interview-feedback")
    public String interviewFeedback(@ModelAttribute("form") ApplicationForm form,
                                     Model model,
                                     @PathVariable("applicationId") long applicationId,
                                     UserResource user) {

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());

        addApplicationAndSectionsInternalWithOrgDetails(application, competition, user, model, form, userApplicationRoles, Optional.of(Boolean.FALSE));
        ProcessRoleResource userApplicationRole = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();

        applicationModelPopulator.addOrganisationAndUserFinanceDetails(competition.getId(), applicationId, user, model, form, userApplicationRole.getOrganisationId());

        model.addAttribute("scores", assessorFormInputResponseRestService.getApplicationAssessmentAggregate(applicationId).getSuccess());
        model.addAttribute("feedback", assessmentRestService.getApplicationFeedback(applicationId)
                .getSuccess()
                .getFeedback()
        );

            return "application-interview-feedback";
    }

    private void addApplicationAndSectionsInternalWithOrgDetails(final ApplicationResource application,
                                                                 final CompetitionResource competition,
                                                                 final UserResource user, final Model model,
                                                                 final ApplicationForm form,
                                                                 List<ProcessRoleResource> userApplicationRoles,
                                                                 final Optional<Boolean> markAsCompleteEnabled) {
        addApplicationAndSectionsInternalWithOrgDetails(application, competition, user, Optional.empty(), Optional.empty(), model, form, userApplicationRoles, markAsCompleteEnabled);
    }

    private void addApplicationAndSectionsInternalWithOrgDetails(final ApplicationResource application,
                                                                 final CompetitionResource competition,
                                                                 final UserResource user,
                                                                 Optional<SectionResource> section,
                                                                 Optional<Long> currentQuestionId,
                                                                 final Model model,
                                                                 final ApplicationForm form,
                                                                 List<ProcessRoleResource> userApplicationRoles,
                                                                 final Optional<Boolean> markAsCompleteEnabled) {
        organisationDetailsModelPopulator.populateModel(model, application.getId(), userApplicationRoles);
        applicationModelPopulator.addApplicationAndSections(application, competition, user, section, currentQuestionId, model, form, userApplicationRoles, markAsCompleteEnabled);
    }

}
