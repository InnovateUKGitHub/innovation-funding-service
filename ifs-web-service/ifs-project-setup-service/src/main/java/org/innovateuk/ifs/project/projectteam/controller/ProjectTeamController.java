package org.innovateuk.ifs.project.projectteam.controller;


import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.project.projectteam.ProjectTeamRestService;
import org.innovateuk.ifs.project.projectteam.populator.ProjectTeamViewModelPopulator;
import org.innovateuk.ifs.projectteam.form.ProjectTeamForm;
import org.innovateuk.ifs.projectteam.util.ProjectInviteHelper;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.String.format;

/**
 * This controller will handle all requests that are related to the project team.
 */

@Controller
@RequestMapping("/project")
public class ProjectTeamController {

    @Autowired
    private ProjectTeamViewModelPopulator projectTeamPopulator;
    @Autowired
    private ProjectTeamRestService projectTeamRestService;
    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;
    @Autowired
    private ProjectInviteHelper projectInviteHelper;

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_TEAM_SECTION')")
    @GetMapping("/{projectId}/team")
    public String viewProjectTeam(@ModelAttribute(value = "form", binding = false) ProjectTeamForm form,
                                  BindingResult bindingResult,
                                  @PathVariable("projectId") final long projectId,
                                  Model model,
                                  UserResource loggedInUser) {
        model.addAttribute("model", projectTeamPopulator.populate(projectId, loggedInUser));
        return "projectteam/project-team";
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_TEAM_SECTION')")
    @PostMapping(value = "/{projectId}/team", params = "remove-team-member")
    public String removeUser(@PathVariable("projectId") final long projectId,
                             @RequestParam("remove-team-member") final long userId) {
        projectTeamRestService.removeUser(projectId, userId).getSuccess();
        return "redirect:/project/" + projectId + "/team";
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_TEAM_SECTION')")
    @PostMapping(value = "/{projectId}/team", params = "remove-invite")
    public String removeInvite(@PathVariable("projectId") final long projectId,
                               @RequestParam("remove-invite") final long inviteId) {
        projectTeamRestService.removeInvite(projectId, inviteId).getSuccess();
        return "redirect:/project/" + projectId + "/team";
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_TEAM_SECTION')")
    @PostMapping(value = "/{projectId}/team", params = "resend-invite")
    public String resendInvite(@PathVariable("projectId") final long projectId,
                               @RequestParam("resend-invite") final long inviteId,
                               HttpServletResponse response) {
        projectInviteHelper.resendInvite(inviteId, projectId, (project, projectInviteResource) -> projectTeamRestService.inviteProjectMember(project, projectInviteResource).toServiceResult());
        cookieFlashMessageFilter.setFlashMessage(response, "emailSent");
        return "redirect:/project/" + projectId + "/team";
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
        return "projectteam/project-team";
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_TEAM_SECTION')")
    @PostMapping(value = "/{projectId}/team", params = "close-add-team-member-form")
    public String closeAddTeamMemberForm(@PathVariable("projectId") final long projectId) {
        return format("redirect:/project/%d/team", projectId);
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
            return "projectteam/project-team";
        };

        Supplier<String> successView = () -> format("redirect:/project/%d/team", projectId);

        return projectInviteHelper.sendInvite(form.getName(), form.getEmail(), loggedInUser, validationHandler,
                failureView, successView, projectId, organisationId);
    }
}
