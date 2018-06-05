package org.innovateuk.ifs.application.summary.controller;

import org.innovateuk.ifs.application.summary.populator.ApplicationInterviewSummaryViewModelPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller will handle all requests that are related to the application summary for an assessor.
 */
@Controller
@SecuredBySpring(value="Controller", description = "Assessor can view an application summary for interview panel", securedType = ApplicationInterviewSummaryController.class)
@RequestMapping("/application")
public class ApplicationInterviewSummaryController {

    private ApplicationInterviewSummaryViewModelPopulator applicationInterviewSummaryViewModelPopulator;


    public ApplicationInterviewSummaryController() {
    }

    @Autowired
    public ApplicationInterviewSummaryController(ApplicationInterviewSummaryViewModelPopulator applicationInterviewSummaryViewModelPopulator) {
        this.applicationInterviewSummaryViewModelPopulator = applicationInterviewSummaryViewModelPopulator;
    }

    @SecuredBySpring(value = "READ", description = "Assessors have permission to view the application summary page")
    @PreAuthorize("hasAnyAuthority('assessor')")
    @GetMapping("/{applicationId}/assessor-summary")
    public String applicationSummary(
                                     Model model,
                                     @PathVariable("applicationId") long applicationId,
                                     UserResource user) {

//        ApplicationResource application = applicationService.getById(applicationId);
//        CompetitionResource competition = competitionService.getById(application.getCompetition());
//        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
//        OrganisationResource leadOrganisation = applicationService.getLeadOrganisation(applicationId);
//        UserResource leadApplicantUser = getLeadApplicant(applicationId);
//        ProcessRoleResource leadUserApplicationRole = userRestService.findProcessRole(leadApplicantUser.getId(), applicationId).getSuccess();
//
//        applicationModelPopulator.addApplicationAndSectionsInternalWithOrgDetails(application, competition, leadApplicantUser, model, form, userApplicationRoles, Optional.of(Boolean.FALSE));
//        applicationModelPopulator.addOrganisationAndUserFinanceDetails(competition.getId(), applicationId, leadApplicantUser, model, form, leadOrganisation.getId());
//        applicationModelPopulator.addFeedbackAndScores(model, applicationId);

//        model.addAttribute("interviewFeedbackViewModel", interviewFeedbackViewModelPopulator.populate(applicationId, leadUserApplicationRole, competition.getCompetitionStatus().isFeedbackReleased(), true));
        model.addAttribute("applicationInterviewSummaryViewModel", applicationInterviewSummaryViewModelPopulator.populate(applicationId));
        return "application-interview-summary";
    }

//    private UserResource getLeadApplicant(long applicationId) {
//        ApplicationTeamResource applicationTeamResource = applicationSummaryRestService.getApplicationTeam(applicationId).getSuccess();
//        List<ApplicationTeamUserResource> applicationTeamUserResource = applicationTeamResource.getLeadOrganisation().getUsers();
//
//        ApplicationTeamUserResource leadUser = applicationTeamUserResource.stream()
//                .filter(teamUserResource -> teamUserResource.getLead())
//                .collect(Collectors.toList())
//                .get(0);
//
//        return userRestService.findUserByEmail(leadUser.getEmail()).getSuccess();
//    }
}