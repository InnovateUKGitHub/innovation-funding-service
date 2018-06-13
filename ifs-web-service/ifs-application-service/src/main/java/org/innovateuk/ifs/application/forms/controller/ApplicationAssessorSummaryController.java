package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.forms.populator.InterviewFeedbackViewModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationTeamResource;
import org.innovateuk.ifs.application.resource.ApplicationTeamUserResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
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
import java.util.stream.Collectors;

/**
 * This controller will handle all requests that are related to the application summary for an assessor.
 */
@Controller
@SecuredBySpring(value="Controller", description = "Assessor can view an application summary for interview panel", securedType = ApplicationAssessorSummaryController.class)
@RequestMapping("/application")
public class ApplicationAssessorSummaryController {

    private ProcessRoleService processRoleService;
    private ApplicationService applicationService;
    private CompetitionService competitionService;
    private ApplicationModelPopulator applicationModelPopulator;
    private UserRestService userRestService;
    private InterviewFeedbackViewModelPopulator interviewFeedbackViewModelPopulator;
    private ApplicationSummaryRestService applicationSummaryRestService;

    public ApplicationAssessorSummaryController() {
    }

    @Autowired
    public ApplicationAssessorSummaryController(ProcessRoleService processRoleService,
                                                ApplicationService applicationService,
                                                CompetitionService competitionService,
                                                ApplicationModelPopulator applicationModelPopulator,
                                                UserRestService userRestService,
                                                InterviewFeedbackViewModelPopulator interviewFeedbackViewModelPopulator,
                                                ApplicationSummaryRestService applicationSummaryRestService) {
        this.processRoleService = processRoleService;
        this.applicationService = applicationService;
        this.competitionService = competitionService;
        this.applicationModelPopulator = applicationModelPopulator;
        this.userRestService = userRestService;
        this.interviewFeedbackViewModelPopulator = interviewFeedbackViewModelPopulator;
        this.applicationSummaryRestService = applicationSummaryRestService;
    }

    @SecuredBySpring(value = "READ", description = "Assessors have permission to view the application summary page")
    @PreAuthorize("hasAnyAuthority('assessor')")
    @GetMapping("/{applicationId}/assessor-summary")
    public String applicationSummary(@ModelAttribute("form") ApplicationForm form,
                                     Model model,
                                     @PathVariable("applicationId") long applicationId,
                                     UserResource user) {

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        OrganisationResource leadOrganisation = applicationService.getLeadOrganisation(applicationId);
        UserResource leadApplicantUser = getLeadApplicant(applicationId);
        ProcessRoleResource leadUserApplicationRole = userRestService.findProcessRole(leadApplicantUser.getId(), applicationId).getSuccess();

        applicationModelPopulator.addApplicationAndSectionsInternalWithOrgDetails(application, competition, leadApplicantUser, model, form, userApplicationRoles, Optional.of(Boolean.FALSE));
        applicationModelPopulator.addOrganisationAndUserFinanceDetails(competition.getId(), applicationId, leadApplicantUser, model, form, leadOrganisation.getId());
        applicationModelPopulator.addFeedbackAndScores(model, applicationId);

        model.addAttribute("interviewFeedbackViewModel", interviewFeedbackViewModelPopulator.populate(applicationId, leadUserApplicationRole, competition.getCompetitionStatus().isFeedbackReleased(), true));
        return "application-assessor-summary";
    }

    private UserResource getLeadApplicant(long applicationId) {
        ApplicationTeamResource applicationTeamResource = applicationSummaryRestService.getApplicationTeam(applicationId).getSuccess();
        List<ApplicationTeamUserResource> applicationTeamUserResource = applicationTeamResource.getLeadOrganisation().getUsers();

        ApplicationTeamUserResource leadUser = applicationTeamUserResource.stream()
                .filter(teamUserResource -> teamUserResource.getLead())
                .collect(Collectors.toList())
                .get(0);

        return userRestService.findUserByEmail(leadUser.getEmail()).getSuccess();
    }
}