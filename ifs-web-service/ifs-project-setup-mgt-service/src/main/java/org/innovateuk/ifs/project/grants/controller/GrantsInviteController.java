package org.innovateuk.ifs.project.grants.controller;

import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.grants.service.GrantsInviteRestService;
import org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource;
import org.innovateuk.ifs.project.grants.form.GrantsSendInviteForm;
import org.innovateuk.ifs.project.grants.viewmodel.GrantsInviteSendViewModel;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

@Controller
@RequestMapping("/project/{projectId}/grants/invite")
public class GrantsInviteController {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private GrantsInviteRestService grantsInviteRestService;

    @GetMapping("/send")
    public String inviteForm(Model model, @PathVariable long projectId, @ModelAttribute("form") GrantsSendInviteForm form) {
        model.addAttribute("model", new GrantsInviteSendViewModel(projectRestService.getProjectById(projectId).getSuccess()));
        return "project/grants-invite/invite";
    }

    @PostMapping("/send")
    public String sendInvite(Model model, @PathVariable long projectId, @ModelAttribute("form") GrantsSendInviteForm form,
                             BindingResult bindingResult, ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> inviteForm(model, projectId, form);
        Supplier<String> successView = () -> "redirect:/";
        GrantsInviteResource resource = new GrantsInviteResource(form.getFirstName() + form.getLastName(), form.getEmail(), form.getRole());
        validationHandler.addAnyErrors(grantsInviteRestService.invitePartnerOrganisation(projectId, resource));
        return validationHandler.failNowOrSucceedWith(failureView, successView);
    }
}
