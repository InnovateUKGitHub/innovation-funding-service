package org.innovateuk.ifs.project.projectteam.controller;


import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.project.projectteam.ProjectTeamRestService;
import org.innovateuk.ifs.project.projectteam.populator.ProjectTeamViewModelPopulator;
import org.innovateuk.ifs.projectteam.form.ProjectTeamForm;
import org.innovateuk.ifs.projectteam.util.ProjectInviteHelper;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.String.format;

/**
 * This controller will handle all requests that are related to the project team.
 */

@Controller
@RequestMapping("/competition/{competitionId}/project")
public class ProjectTeamController {

    private ProjectTeamViewModelPopulator projectTeamPopulator;
    private ProjectTeamRestService projectTeamRestService;
    private CookieFlashMessageFilter cookieFlashMessageFilter;
    private ProjectInviteHelper projectInviteHelper;

    public ProjectTeamController(ProjectTeamViewModelPopulator projectTeamPopulator,
                                 ProjectTeamRestService projectTeamRestService,
                                 CookieFlashMessageFilter cookieFlashMessageFilter,
                                 ProjectInviteHelper projectInviteHelper
                                 ) {
        this.projectTeamPopulator = projectTeamPopulator;
        this.projectTeamRestService = projectTeamRestService;
        this.cookieFlashMessageFilter = cookieFlashMessageFilter;
        this.projectInviteHelper = projectInviteHelper;
    }

    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
    @SecuredBySpring(value = "VIEW_PROJECT_TEAM", description = "Project finance, comp admin, support, innovation lead and stakeholders can view the project team page")
    @GetMapping("/{projectId}/team")
    public String viewProjectTeam(@ModelAttribute(value = "form", binding = false) ProjectTeamForm form,
                                  BindingResult bindingResult,
                                  @PathVariable("projectId") long projectId,
                                  Model model,
                                  UserResource loggedInUser) {
        model.addAttribute("model", projectTeamPopulator.populate(projectId, loggedInUser));
        return "projectteam/project-team";
    }

    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
    @SecuredBySpring(value = "VIEW_PROJECT_TEAM", description = "Project finance, comp admin, support, innovation lead and stakeholders can remove invites")
    @PostMapping(value = "/{projectId}/team", params = "remove-invite")
    public String removeInvite(@PathVariable("projectId") long projectId,
                               @PathVariable("competitionId") long competitionId,
                               @RequestParam("remove-invite") long inviteId) {
        projectTeamRestService.removeInvite(projectId, inviteId).getSuccess();
        return format("redirect:/competition/%d/project/%d/team", competitionId, projectId);
    }

    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
    @SecuredBySpring(value = "VIEW_PROJECT_TEAM", description = "Project finance, comp admin, support, innovation lead and stakeholders can add team members")
    @PostMapping(value = "/{projectId}/team", params = "add-team-member")
    public String openAddTeamMemberForm(@ModelAttribute(value = "form") ProjectTeamForm form,
                                        BindingResult bindingResult,
                                        @PathVariable("projectId") long projectId,
                                        @PathVariable("competitionId") long competitionId,
                                        @RequestParam("add-team-member") long organisationId,
                                        Model model,
                                        UserResource loggedInUser) {
        model.addAttribute("model", projectTeamPopulator.populate(projectId, loggedInUser)
                .openAddTeamMemberForm(organisationId));
        return "projectteam/project-team";
    }

    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
    @SecuredBySpring(value = "VIEW_PROJECT_TEAM", description = "Project finance, comp admin, support, innovation lead and stakeholders can add team members")
    @PostMapping(value = "/{projectId}/team", params = "close-add-team-member-form")
    public String closeAddTeamMemberForm(@PathVariable("projectId") long projectId,
                                         @PathVariable("competitionId") long competitionId) {
        return format("redirect:/competition/%d/project/%d/team", competitionId, projectId);
    }

    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
    @SecuredBySpring(value = "VIEW_PROJECT_TEAM", description = "Project finance, comp admin, support, innovation lead and stakeholders can add team members")
    @PostMapping(value = "/{projectId}/team", params = "invite-to-project")
    public String inviteToProject(@Valid @ModelAttribute("form") ProjectTeamForm form,
                                  BindingResult bindingResult,
                                  ValidationHandler validationHandler,
                                  @PathVariable("projectId") long projectId,
                                  @PathVariable("competitionId") long competitionId,
                                  @RequestParam("invite-to-project") long organisationId,
                                  Model model,
                                  UserResource loggedInUser) {
        Supplier<String> failureView = () -> {
            model.addAttribute("model", projectTeamPopulator.populate(projectId, loggedInUser)
                    .openAddTeamMemberForm(organisationId));
            return "projectteam/project-team";
        };

        Supplier<String> successView = () -> format("redirect:/competition/%d/project/%d/team", competitionId, projectId);

        return projectInviteHelper.sendInvite(form.getName(), form.getEmail(), loggedInUser, validationHandler,
                          failureView, successView, projectId, organisationId,
                          (project, projectInviteResource) -> projectTeamRestService.inviteProjectMember(project, projectInviteResource).toServiceResult());
    }

    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
    @SecuredBySpring(value = "VIEW_PROJECT_TEAM", description = "Project finance, comp admin, support, innovation lead and stakeholders can resend invites")
    @PostMapping(value = "/{projectId}/team", params = "resend-invite")
    public String resendInvite(@PathVariable("projectId") long projectId,
                               @PathVariable("competitionId") long competitionId,
                               @RequestParam("resend-invite") long inviteId,
                               HttpServletResponse response) {
        projectInviteHelper.resendInvite(inviteId, projectId, (project, projectInviteResource) -> projectTeamRestService.inviteProjectMember(project, projectInviteResource).toServiceResult());
        cookieFlashMessageFilter.setFlashMessage(response, "emailSent");
        return format("redirect:/competition/%d/project/%d/team", competitionId, projectId);
    }

}

