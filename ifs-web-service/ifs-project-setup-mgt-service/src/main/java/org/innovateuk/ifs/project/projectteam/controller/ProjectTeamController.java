package org.innovateuk.ifs.project.projectteam.controller;


import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.projectteam.ProjectTeamRestService;
import org.innovateuk.ifs.project.projectteam.populator.ProjectTeamViewModelPopulator;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.projectdetails.ProjectDetailsService;
import org.innovateuk.ifs.projectteam.form.ProjectTeamForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * This controller will handle all requests that are related to the project team.
 */

@Controller
@RequestMapping("/competition/{competitionId}/project")
public class ProjectTeamController {

    private ProjectTeamViewModelPopulator projectTeamPopulator;
    private ProjectDetailsService projectDetailsService;
    private ProjectService projectService;
    private OrganisationRestService organisationRestService;
    private ProjectTeamRestService projectTeamRestService;
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    public ProjectTeamController(ProjectTeamViewModelPopulator projectTeamPopulator,
                                 ProjectDetailsService projectDetailsService,
                                 ProjectService projectService,
                                 OrganisationRestService organisationRestService,
                                 ProjectTeamRestService projectTeamRestService,
                                 CookieFlashMessageFilter cookieFlashMessageFilter) {
        this.projectTeamPopulator = projectTeamPopulator;
        this.projectDetailsService = projectDetailsService;
        this.projectService = projectService;
        this.organisationRestService = organisationRestService;
        this.projectTeamRestService = projectTeamRestService;
        this.cookieFlashMessageFilter = cookieFlashMessageFilter;
    }

    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
    @SecuredBySpring(value = "VIEW_PROJECT_TEAM", description = "Project finance, comp admin, support, innovation lead and stakeholders can view the project team page")
    @GetMapping("/{projectId}/team")
    public String viewProjectTeam(@ModelAttribute(value = "form", binding = false) ProjectTeamForm form,
                                  BindingResult bindingResult,
                                  @PathVariable("projectId") final long projectId,
                                  Model model,
                                  UserResource loggedInUser) {
        model.addAttribute("model", projectTeamPopulator.populate(projectId, loggedInUser));
        return "projectteam/project-team";
    }

    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
    @SecuredBySpring(value = "VIEW_PROJECT_TEAM", description = "Project finance, comp admin, support, innovation lead and stakeholders can remove invites")
    @PostMapping(value = "/{projectId}/team", params = "remove-invite")
    public String removeInvite(@PathVariable("projectId") final long projectId,
                               @RequestParam("remove-invite") final long inviteId) {
        projectTeamRestService.removeInvite(projectId, inviteId).getSuccess();
        return "redirect:/project/" + projectId + "/team";
    }

    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
    @SecuredBySpring(value = "VIEW_PROJECT_TEAM", description = "Project finance, comp admin, support, innovation lead and stakeholders can add team members")
    @PostMapping(value = "/{projectId}/team", params = "add-team-member")
    public String openAddTeamMemberForm(@ModelAttribute(value = "form") ProjectTeamForm form,
                                        BindingResult bindingResult,
                                        @PathVariable("projectId") final long projectId,
                                        @RequestParam("add-team-member") final long organisationId,
                                        Model model,
                                        UserResource loggedInUser) {
        model.addAttribute("model", projectTeamPopulator.populate(projectId, loggedInUser)
                .openAddTeamMemberForm(organisationId));
        return "projectteam/project-team";
    }

    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
    @SecuredBySpring(value = "VIEW_PROJECT_TEAM", description = "Project finance, comp admin, support, innovation lead and stakeholders can add team members")
    @PostMapping(value = "/{projectId}/team", params = "close-add-team-member-form")
    public String closeAddTeamMemberForm(@PathVariable("projectId") final long projectId,
                                         @PathVariable("competitionId") final long competitionId) {
        return String.format("redirect:/competition/%d/project/%d/team", competitionId, projectId);
    }

    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
    @SecuredBySpring(value = "VIEW_PROJECT_TEAM", description = "Project finance, comp admin, support, innovation lead and stakeholders can add team members")
    @PostMapping(value = "/{projectId}/team", params = "invite-to-project")
    public String inviteToProject(@Valid @ModelAttribute("form") ProjectTeamForm form,
                                  BindingResult bindingResult,
                                  ValidationHandler validationHandler,
                                  @PathVariable("projectId") final long projectId,
                                  @PathVariable("competitionId") final long competitionId,
                                  @RequestParam("invite-to-project") final long organisationId,
                                  Model model,
                                  UserResource loggedInUser) {
        Supplier<String> failureView = () -> {
            model.addAttribute("model", projectTeamPopulator.populate(projectId, loggedInUser)
                    .openAddTeamMemberForm(organisationId));
            return "projectteam/project-team";
        };

        Supplier<String> successView = () -> String.format("redirect:/competition/%d/project/%d/team", competitionId, projectId);

        return sendInvite(form.getName(), form.getEmail(), loggedInUser, validationHandler,
                          failureView, successView, projectId, organisationId,
                          (project, projectInviteResource) -> projectTeamRestService.inviteProjectMember(project, projectInviteResource).toServiceResult());
    }

    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
    @SecuredBySpring(value = "VIEW_PROJECT_TEAM", description = "Project finance, comp admin, support, innovation lead and stakeholders can resend invites")
    @PostMapping(value = "/{projectId}/team", params = "resend-invite")
    public String resendInvite(@PathVariable("projectId") final long projectId,
                               @PathVariable("competitionId") final long competitionId,
                               @RequestParam("resend-invite") final long inviteId,
                               HttpServletResponse response) {
        resendInvite(inviteId, projectId, (project, projectInviteResource) -> projectTeamRestService.inviteProjectMember(project, projectInviteResource).toServiceResult());
        cookieFlashMessageFilter.setFlashMessage(response, "emailSent");
        return "redirect:/competition/" + competitionId + "/project/" + projectId + "/team";
    }

    private void resendInvite(Long id, Long projectId, BiFunction<Long, ProjectUserInviteResource, ServiceResult<Void>> sendInvite) {
        Optional<ProjectUserInviteResource> existingInvite = projectDetailsService
                .getInvitesByProject(projectId)
                .getSuccess()
                .stream()
                .filter(i -> id.equals(i.getId()))
                .findFirst();

        existingInvite
                .ifPresent(i -> sendInvite.apply(projectId, existingInvite.get()).getSuccess());
    }


    private String sendInvite(String inviteName, String inviteEmail, UserResource loggedInUser, ValidationHandler validationHandler,
                              Supplier<String> failureView, Supplier<String> successView, Long projectId, Long organisation,
                              BiFunction<Long, ProjectUserInviteResource, ServiceResult<Void>> sendInvite) {

        validateIfTryingToInviteSelf(loggedInUser.getEmail(), inviteEmail, validationHandler);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ProjectUserInviteResource invite = createProjectInviteResourceForNewContact(projectId, inviteName, inviteEmail, organisation);

            ServiceResult<Void> saveResult = projectDetailsService.saveProjectInvite(invite);

            return validationHandler.addAnyErrors(saveResult, asGlobalErrors()).failNowOrSucceedWith(failureView, () -> {

                Optional<ProjectUserInviteResource> savedInvite = getSavedInvite(projectId, invite);

                if (savedInvite.isPresent()) {
                    ServiceResult<Void> inviteResult = sendInvite.apply(projectId, savedInvite.get());
                    return validationHandler.addAnyErrors(inviteResult).failNowOrSucceedWith(failureView, successView);
                } else {
                    return validationHandler.failNowOrSucceedWith(failureView, successView);
                }
            });
        });
    }

    private void validateIfTryingToInviteSelf(String loggedInUserEmail, String inviteEmail,
                                              ValidationHandler validationHandler) {
        if (equalsIgnoreCase(loggedInUserEmail, inviteEmail)) {
            validationHandler.addAnyErrors(serviceFailure(CommonFailureKeys.PROJECT_SETUP_CANNOT_INVITE_SELF));
        }
    }

    private ProjectUserInviteResource createProjectInviteResourceForNewContact(Long projectId, String name,
                                                                               String email, Long organisationId) {
        ProjectResource projectResource = projectService.getById(projectId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        OrganisationResource organisationResource = organisationRestService.getOrganisationById(organisationId).getSuccess();

        ProjectUserInviteResource inviteResource = new ProjectUserInviteResource();

        inviteResource.setProject(projectId);
        inviteResource.setName(name);
        inviteResource.setEmail(email);
        inviteResource.setOrganisation(organisationId);
        inviteResource.setOrganisationName(organisationResource.getName());
        inviteResource.setApplicationId(projectResource.getApplication());
        inviteResource.setLeadOrganisationId(leadOrganisation.getId());

        return inviteResource;
    }

    private Optional<ProjectUserInviteResource> getSavedInvite(Long projectId, ProjectUserInviteResource invite) {
        return simpleFindFirst(projectDetailsService.getInvitesByProject(projectId).getSuccess(),
                               i -> i.getEmail().equals(invite.getEmail()));
    }
}

