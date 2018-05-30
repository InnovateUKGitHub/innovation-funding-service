package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.forms.form.InterviewResponseForm;
import org.innovateuk.ifs.application.forms.populator.InterviewFeedbackViewModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationTeamResource;
import org.innovateuk.ifs.application.resource.ApplicationTeamUserResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This controller will handle all requests that are related to the application summary for an assessor.
 */
@Controller
@SecuredBySpring(value="Controller", description = "Assessor can view an application summary for interview panel", securedType = ApplicationAssessorSummaryController.class)
@RequestMapping("/application")
public class ApplicationAssessorSummaryController {

    private ProcessRoleService processRoleService;
    private SectionService sectionService;
    private ApplicationService applicationService;
    private CompetitionService competitionService;
    private ApplicationModelPopulator applicationModelPopulator;
    private FormInputResponseService formInputResponseService;
    private FormInputResponseRestService formInputResponseRestService;
    private UserRestService userRestService;
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;
    private AssessmentRestService assessmentRestService;
    private ProjectService projectService;
    private InterviewFeedbackViewModelPopulator interviewFeedbackViewModelPopulator;
    private ApplicationSummaryRestService applicationSummaryRestService;

    public ApplicationAssessorSummaryController() {
    }

    @Autowired
    public ApplicationAssessorSummaryController(ProcessRoleService processRoleService,
                                                SectionService sectionService,
                                                ApplicationService applicationService,
                                                CompetitionService competitionService,
                                                ApplicationModelPopulator applicationModelPopulator,
                                                FormInputResponseService formInputResponseService,
                                                FormInputResponseRestService formInputResponseRestService,
                                                UserRestService userRestService,
                                                AssessorFormInputResponseRestService assessorFormInputResponseRestService,
                                                AssessmentRestService assessmentRestService,
                                                ProjectService projectService,
                                                InterviewFeedbackViewModelPopulator interviewFeedbackViewModelPopulator,
                                                ApplicationSummaryRestService applicationSummaryRestService) {
        this.processRoleService = processRoleService;
        this.sectionService = sectionService;
        this.applicationService = applicationService;
        this.competitionService = competitionService;
        this.applicationModelPopulator = applicationModelPopulator;
        this.formInputResponseService = formInputResponseService;
        this.formInputResponseRestService = formInputResponseRestService;
        this.userRestService = userRestService;
        this.assessorFormInputResponseRestService = assessorFormInputResponseRestService;
        this.assessmentRestService = assessmentRestService;
        this.projectService = projectService;
        this.interviewFeedbackViewModelPopulator = interviewFeedbackViewModelPopulator;
        this.applicationSummaryRestService = applicationSummaryRestService;
    }

    @PreAuthorize("hasAnyAuthority('assessor')")
    @GetMapping("/{applicationId}/assessor-summary")
    public String applicationSummary(@ModelAttribute("form") ApplicationForm form,
                                     @ModelAttribute("interviewResponseForm") InterviewResponseForm interviewResponseForm,
                                     BindingResult bindingResult,
                                     ValidationHandler validationHandler,
                                     Model model,
                                     @PathVariable("applicationId") long applicationId,
                                     UserResource user) {

        List<FormInputResponseResource> responses = formInputResponseRestService.getResponsesByApplicationId(applicationId).getSuccess();
        model.addAttribute("incompletedSections", sectionService.getInCompleted(applicationId));
        model.addAttribute("responses", formInputResponseService.mapFormInputResponsesToFormInput(responses));

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());

        OrganisationResource leadOrganisation = applicationService.getLeadOrganisation(applicationId);
        ApplicationTeamResource applicationTeamResource = applicationSummaryRestService.getApplicationTeam(applicationId).getSuccess();
        List<ApplicationTeamUserResource> applicationTeamUserResource = applicationTeamResource.getLeadOrganisation().getUsers();

        ApplicationTeamUserResource leadUser = applicationTeamUserResource.stream()
                .filter(teamUserResource -> teamUserResource.getLead())
                .collect(Collectors.toList())
                .get(0);

        UserResource leadApplicantUser = userRestService.findUserByEmail(leadUser.getEmail()).getSuccess();

        applicationModelPopulator.addApplicationAndSectionsInternalWithOrgDetails(application, competition, leadApplicantUser, model, form, userApplicationRoles, Optional.of(Boolean.FALSE));
        applicationModelPopulator.addOrganisationAndUserFinanceDetails(competition.getId(), applicationId, leadApplicantUser, model, form, leadOrganisation.getId());

        ProcessRoleResource leadUserApplicationRole = userRestService.findProcessRole(leadApplicantUser.getId(), applicationId).getSuccess();

        model.addAttribute("applicationReadyForSubmit", applicationService.isApplicationReadyForSubmit(application.getId()));

        ProjectResource project = projectService.getByApplicationId(applicationId);
        boolean projectWithdrawn = (project != null && project.isWithdrawn());
        model.addAttribute("projectWithdrawn", projectWithdrawn);

        addFeedbackAndScores(model, applicationId);
        model.addAttribute("interviewFeedbackViewModel", interviewFeedbackViewModelPopulator.populate(applicationId, leadUserApplicationRole, true));
        return "application-interview-feedback";
    }

    private void addFeedbackAndScores(Model model, long applicationId) {
        model.addAttribute("scores", assessorFormInputResponseRestService.getApplicationAssessmentAggregate(applicationId).getSuccess());
        model.addAttribute("feedback", assessmentRestService.getApplicationFeedback(applicationId)
                .getSuccess()
                .getFeedback()
        );
    }
}