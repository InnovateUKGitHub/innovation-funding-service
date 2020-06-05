package org.innovateuk.ifs.project.invitations.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.grants.service.GrantsInviteRestService;
import org.innovateuk.ifs.grantsinvite.resource.SentGrantsInviteResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.invitations.form.ResendInvitationForm;
import org.innovateuk.ifs.project.invitations.populator.ManageInvitationsModelPopulator;
import org.innovateuk.ifs.project.invitations.viewmodel.ManageInvitationsViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/project/{projectId}")
public class ManageInvitationsController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private GrantsInviteRestService grantsInviteRestService;

    @Autowired
    private ManageInvitationsModelPopulator manageInvitationsModelPopulator;

    @SecuredBySpring(value = "MANAGE_INVITATIONS", description = "Only project finance and ifs admin users can manage invitations")
    @PreAuthorize("hasAnyAuthority('project_finance', 'ifs_administrator')")
    @GetMapping("/manage-invitations")
    public String viewInvitations(
            Model model,
            @PathVariable("projectId") long projectId,
            UserResource loggedInUser) {

        ProjectResource project = projectService.getById(projectId);
        List<SentGrantsInviteResource> grants = grantsInviteRestService.getAllForProject(projectId).getSuccess();

        ManageInvitationsViewModel viewModel = manageInvitationsModelPopulator.populateManageInvitationsViewModel(project, grants);

        model.addAttribute("model", viewModel);
        return "project/manage-invitations";
    }

    @SecuredBySpring(value = "MANAGE_INVITATIONS", description = "Only project finance and ifs admin users can manage invitations")
    @PreAuthorize("hasAnyAuthority('project_finance', 'ifs_administrator')")
    @PostMapping("/manage-invitations")
    public String resendInvitation(
            Model model, @PathVariable long projectId, @ModelAttribute("form") ResendInvitationForm form,
            BindingResult bindingResult, ValidationHandler validationHandler) {

        grantsInviteRestService.resendInvite(form.getProjectId(), form.getInviteId());

        return String.format("redirect:/project/%s/manage-invitations", projectId);
    }

}
