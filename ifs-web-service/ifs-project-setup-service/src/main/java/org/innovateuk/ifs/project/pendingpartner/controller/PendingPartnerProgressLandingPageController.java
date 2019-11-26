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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

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
    public String progressLandingPage(@PathVariable long projectId, @PathVariable long organisationId, Model model) {
        model.addAttribute("model", populator.populate(projectId, organisationId));
        return "project/pending-partner-progress/landing-page";
    }

    @PostMapping()
    public String redirectToJoinProjectConfirm(@PathVariable long projectId, @PathVariable long organisationId){
        return "redirect:/project/" + projectId + "/organisation/" + organisationId+ "/pending-partner-progress/join-project-confirm-submit";
    }

    @GetMapping("/join-project-confirm-submit")
    public String joinProjectConfirm(@PathVariable long projectId, @PathVariable long organisationId, @ModelAttribute("form") JoinProjectForm form, Model model){
        model.addAttribute("projectId", projectId);
        model.addAttribute("organisationId", organisationId);
        return "project/pending-partner-progress/join-project-confirm-submit";
    }

    @PostMapping("/join-project-confirm-submit")
    public String joinProject(@PathVariable long projectId,
                              @PathVariable long organisationId,
                              Model model,
                              @ModelAttribute("form") JoinProjectForm form,
                              BindingResult bindingResult,
                              ValidationHandler validationHandler){
        Supplier<String> failureView = () -> joinProjectConfirm(projectId, organisationId, form, model);
        Supplier<String> successView = () -> "redirect:/project/" + projectId;
        RestResult<Void> result = pendingPartnerProgressRestService.completePartnerSetup(projectId, organisationId);
        return validationHandler.addAnyErrors(result,fieldErrorsToFieldErrors(), asGlobalErrors()).failNowOrSucceedWith(failureView, successView);
    }
}
