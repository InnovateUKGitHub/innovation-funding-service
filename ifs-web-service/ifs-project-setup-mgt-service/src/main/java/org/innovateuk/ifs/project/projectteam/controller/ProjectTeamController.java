package org.innovateuk.ifs.project.projectteam.controller;


import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.project.invite.service.ProjectPartnerInviteRestService;
import org.innovateuk.ifs.project.projectteam.ProjectTeamRestService;
import org.innovateuk.ifs.project.projectteam.populator.ProjectTeamViewModelPopulator;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.projectteam.form.ProjectTeamForm;
import org.innovateuk.ifs.projectteam.util.ProjectInviteHelper;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/competition/{competitionId}/project/{projectId}/team")
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder', 'comp_finance')")
@SecuredBySpring(value = "VIEW_PROJECT_TEAM", description = "Project finance, comp admin, support, innovation lead and stakeholders can view the project team page")
public class ProjectTeamController {

    @Autowired
    private ProjectTeamViewModelPopulator projectTeamPopulator;
    @Autowired
    private ProjectTeamRestService projectTeamRestService;
    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;
    @Autowired
    private ProjectInviteHelper projectInviteHelper;
    @Autowired
    private PartnerOrganisationRestService partnerOrganisationRestService;
    @Autowired
    private ProjectPartnerInviteRestService projectPartnerInviteRestService;

    @GetMapping
    public String viewProjectTeam(@ModelAttribute(value = "form", binding = false) ProjectTeamForm form,
                                  BindingResult bindingResult,
                                  @PathVariable long projectId,
                                  Model model,
                                  UserResource loggedInUser) {
        model.addAttribute("model", projectTeamPopulator.populate(projectId, loggedInUser));
        return "projectteam/project-team";
    }

    @PostMapping(params = "add-team-member")
    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'support')")
    @SecuredBySpring(value = "EDIT_PROJECT_TEAM", description = "IFS Admin and support users can edit project team.")
    public String openAddTeamMemberForm(@ModelAttribute(value = "form") ProjectTeamForm form,
                                        BindingResult bindingResult,
                                        @PathVariable long projectId,
                                        @PathVariable long competitionId,
                                        @RequestParam("add-team-member") long organisationId,
                                        Model model,
                                        UserResource loggedInUser) {
        model.addAttribute("model", projectTeamPopulator.populate(projectId, loggedInUser)
                .openAddTeamMemberForm(organisationId));
        return "projectteam/project-team";
    }

    @PostMapping(params = "close-add-team-member-form")
    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'support')")
    @SecuredBySpring(value = "EDIT_PROJECT_TEAM", description = "IFS Admin and support users can edit project team.")
    public String closeAddTeamMemberForm(@PathVariable long projectId,
                                         @PathVariable long competitionId) {
        return format("redirect:/competition/%d/project/%d/team", competitionId, projectId);
    }

    @PostMapping(params = "invite-to-project")
    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'support')")
    @SecuredBySpring(value = "EDIT_PROJECT_TEAM", description = "IFS Admin and support users can edit project team.")
    public String inviteToProject(@Valid @ModelAttribute("form") ProjectTeamForm form,
                                  BindingResult bindingResult,
                                  ValidationHandler validationHandler,
                                  @PathVariable long projectId,
                                  @PathVariable long competitionId,
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
                failureView, successView, projectId, organisationId);
    }

    @PostMapping(params = "resend-invite")
    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'support')")
    @SecuredBySpring(value = "EDIT_PROJECT_TEAM", description = "IFS Admin and support users can edit project team.")
    public String resendInvite(@PathVariable long projectId,
                               @PathVariable long competitionId,
                               @RequestParam("resend-invite") long inviteId,
                               HttpServletResponse response) {
        projectInviteHelper.resendInvite(inviteId, projectId, (project, projectInviteResource) -> projectTeamRestService.inviteProjectMember(project, projectInviteResource).toServiceResult());
        cookieFlashMessageFilter.setFlashMessage(response, "emailSent");
        return format("redirect:/competition/%d/project/%d/team", competitionId, projectId);
    }

    @PostMapping(params = "remove-organisation")
    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'project_finance')")
    @SecuredBySpring(value = "EDIT_PROJECT_TEAM", description = "IFS Admin and project finance can edit project team.")
    public String removeOrganisation(@PathVariable("projectId") long projectId,
                                     @PathVariable("competitionId") long competitionId,
                                     @RequestParam("remove-organisation") final long orgId) {
        partnerOrganisationRestService.removePartnerOrganisation(projectId, orgId).getSuccess();
        return format("redirect:/competition/%d/project/%d/team", competitionId, projectId);
    }

    @PostMapping(params = "remove-invite")
    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'support')")
    @SecuredBySpring(value = "EDIT_PROJECT_TEAM", description = "IFS Admin and support users can edit project team.")
    public String removeInvite(@PathVariable long projectId,
                               @PathVariable long competitionId,
                               @RequestParam("remove-invite") long inviteId) {
        projectTeamRestService.removeInvite(projectId, inviteId).getSuccess();
        return format("redirect:/competition/%d/project/%d/team", competitionId, projectId);
    }

    @PostMapping(params = "resend-partner-invite")
    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "PROJECT_PARTNER_CHANGES", description = "Project finance can change the partner organisations.")
    public String resendPartnerInvite(@PathVariable long projectId,
                                      @PathVariable long competitionId,
                                      @RequestParam("resend-partner-invite") long inviteId,
                                      HttpServletResponse response) {
        projectPartnerInviteRestService.resendInvite(projectId, inviteId).getSuccess();
        cookieFlashMessageFilter.setFlashMessage(response, "emailSent");
        return format("redirect:/competition/%d/project/%d/team", competitionId, projectId);
    }

    @PostMapping(params = "remove-partner-invite")
    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "PROJECT_PARTNER_CHANGES", description = "Project finance can change the partner organisations.")
    public String removePartnerInvite(@PathVariable long projectId,
                                      @PathVariable long competitionId,
                                      @RequestParam("remove-partner-invite") long inviteId) {
        projectPartnerInviteRestService.deleteInvite(projectId, inviteId).getSuccess();
        return format("redirect:/competition/%d/project/%d/team", competitionId, projectId);
    }
}

