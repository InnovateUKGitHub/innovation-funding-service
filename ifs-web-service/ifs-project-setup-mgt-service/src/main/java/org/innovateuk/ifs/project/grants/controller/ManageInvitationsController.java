package org.innovateuk.ifs.project.grants.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.grants.service.GrantsInviteRestService;
import org.innovateuk.ifs.project.grants.form.ResendInvitationForm;
import org.innovateuk.ifs.project.grants.populator.ManageInvitationsModelPopulator;
import org.innovateuk.ifs.project.grants.viewmodel.ManageInvitationsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.function.Supplier;

@Controller
@RequestMapping("/project/{projectId}")
public class ManageInvitationsController {

    @Autowired
    private GrantsInviteRestService grantsInviteRestService;

    @Autowired
    private ManageInvitationsModelPopulator manageInvitationsModelPopulator;

    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @SecuredBySpring(value = "MANAGE_INVITATIONS", description = "Only project finance users can manage invitations")
    @PreAuthorize("hasAnyAuthority('project_finance')")
    @GetMapping("/grants/invite")
    public String viewInvitations(
            Model model,
            @PathVariable("projectId") long projectId, @ModelAttribute("form") ResendInvitationForm form) {

        ManageInvitationsViewModel viewModel = manageInvitationsModelPopulator.populateManageInvitationsViewModel(projectId);

        model.addAttribute("model", viewModel);
        return "project/manage-invitations";
    }

    @SecuredBySpring(value = "MANAGE_INVITATIONS", description = "Only project finance users can manage invitations")
    @PreAuthorize("hasAnyAuthority('project_finance')")
    @PostMapping("/grants/invite/resend")
    public String resendInvitation(Model model, @PathVariable long projectId, @ModelAttribute("form") ResendInvitationForm form,
                                   ValidationHandler validationHandler, HttpServletResponse response) {

        Supplier<String> failureView = () -> viewInvitations(model, projectId, form);
        Supplier<String> successView = () -> {
            cookieFlashMessageFilter.setFlashMessage(response, "emailSent");
            return String.format("redirect:/project/%d/grants/invite", projectId);
        };

        validationHandler.addAnyErrors(grantsInviteRestService.resendInvite(form.getProjectId(), form.getInviteId()));
        return validationHandler.failNowOrSucceedWith(failureView, successView);
    }

}
