package org.innovateuk.ifs.project.projectteam.controller;


import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.projectteam.ProjectTeamRestService;
import org.innovateuk.ifs.project.projectteam.form.ProjectTeamForm;
import org.innovateuk.ifs.project.projectteam.populator.ProjectTeamViewModelPopulator;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.projectdetails.ProjectDetailsService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;

/**
 * This controller will handle all requests that are related to the project team.
 */

@Controller
@RequestMapping("/project")
public class ProjectTeamController {

    private ProjectTeamViewModelPopulator projectTeamPopulator;
    private ProjectDetailsService projectDetailsService;
    private ProjectService projectService;
    private OrganisationRestService organisationRestService;
    private ProjectTeamRestService projectTeamRestService;

    ProjectTeamController() {}

    @Autowired
    public ProjectTeamController(ProjectTeamViewModelPopulator projectTeamPopulator, ProjectDetailsService projectDetailsService, ProjectService projectService, OrganisationRestService organisationRestService, ProjectTeamRestService projectTeamRestService) {
        this.projectTeamPopulator = projectTeamPopulator;
        this.projectDetailsService = projectDetailsService;
        this.projectService = projectService;
        this.organisationRestService = organisationRestService;
        this.projectTeamRestService = projectTeamRestService;
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_TEAM_SECTION')")
    @GetMapping("/{projectId}/team")
    public String viewProjectTeam(@ModelAttribute(value = "form", binding = false) ProjectTeamForm form,
                                  BindingResult bindingResult,
                                  @PathVariable("projectId") final long projectId,
                                  Model model,
                                  UserResource loggedInUser) {
        model.addAttribute("model", projectTeamPopulator.populate(projectId, loggedInUser));
        return "project/project-team";
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_TEAM_SECTION')")
    @PostMapping(value = "/{projectId}/team", params = "add-team-member")
    public String openAddTeamMemberForm(@ModelAttribute(value = "form") ProjectTeamForm form,
                                BindingResult bindingResult,
                                @PathVariable("projectId") final long projectId,
                                @RequestParam("add-team-member") final long organisationId,
                                Model model,
                                UserResource loggedInUser) {
        model.addAttribute("model", projectTeamPopulator.populate(projectId, loggedInUser)
                .openAddTeamMemberForm(organisationId));
        return "project/project-team";
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_TEAM_SECTION')")
    @PostMapping(value = "/{projectId}/team", params = "close-add-team-member-form")
    public String closeAddTeamMemberForm(@PathVariable("projectId") final long projectId) {
        return String.format("redirect:/project/%d/team", projectId);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_TEAM_SECTION')")
    @PostMapping(value = "/{projectId}/team", params = "invite-to-project")
    public String inviteToProject(@Valid @ModelAttribute("form") ProjectTeamForm form,
                                  BindingResult bindingResult,
                                  ValidationHandler validationHandler,
                                  @PathVariable("projectId") final long projectId,
                                  @RequestParam("invite-to-project") final long organisationId,
                                  Model model,
                                  UserResource loggedInUser) {
        Supplier<String> failureView = () -> {
            model.addAttribute("model", projectTeamPopulator.populate(projectId, loggedInUser)
                    .openAddTeamMemberForm(organisationId));
            return "project/project-team";
        };

        Supplier<String> successView = () -> String.format("redirect:/project/%d/team", projectId);

        return sendInvite(form.getName(), form.getEmail(), loggedInUser, validationHandler,
                failureView, successView, projectId, organisationId,
                (project, projectInviteResource) -> projectTeamRestService.inviteProjectMember(project, projectInviteResource).toServiceResult());
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

        return projectDetailsService.getInvitesByProject(projectId).getSuccess().stream()
                .filter(i -> i.getEmail().equals(invite.getEmail())).findFirst();
    }
}
