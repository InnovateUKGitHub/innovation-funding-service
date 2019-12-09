package org.innovateuk.ifs.project.pendingpartner.controller;


import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.project.pendingpartner.form.JoinProjectForm;
import org.innovateuk.ifs.project.pendingpartner.populator.PendingPartnerProgressLandingPageViewModelPopulator;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.innovateuk.ifs.project.status.controller.SetupStatusController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.function.Supplier;


@Controller
@RequestMapping("/project/{projectId}/organisation/{organisationId}/pending-partner-progress")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = SetupStatusController.class)
@PreAuthorize("hasAnyAuthority('applicant')")
public class PendingPartnerProgressLandingPageController {

    @Autowired
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;


    @Autowired
    private PendingPartnerProgressLandingPageViewModelPopulator populator;

    @GetMapping
    public String progressLandingPage(@PathVariable long projectId, @PathVariable long organisationId, Model model, @ModelAttribute("form") JoinProjectForm form) {
        model.addAttribute("model", populator.populate(projectId, organisationId));
        return "project/pending-partner-progress/landing-page";
    }

    @PostMapping
    public String joinProject(@PathVariable long projectId,
                              @PathVariable long organisationId,
                              Model model,
                              @ModelAttribute("form") JoinProjectForm form,
                              ValidationHandler validationHandler){
        Supplier<String> failureView = () -> progressLandingPage(projectId, organisationId, model, form);
        Supplier<String> successView = () -> "redirect:/project/" + projectId;
        RestResult<Void> result = pendingPartnerProgressRestService.completePartnerSetup(projectId, organisationId);
        return validationHandler.addAnyErrors(result).failNowOrSucceedWith(failureView, successView);
    }
}
