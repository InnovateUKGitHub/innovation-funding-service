package org.innovateuk.ifs.project.pendingpartner.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.project.pendingpartner.form.ProjectTermsForm;
import org.innovateuk.ifs.project.pendingpartner.populator.ProjectTermsModelPopulator;
import org.innovateuk.ifs.project.pendingpartner.viewmodel.ProjectTermsViewModel;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

import static java.lang.String.format;

@Controller
@RequestMapping("/project/{projectId}/organisation/{organisationId}/terms-and-conditions")
@PreAuthorize("hasAnyAuthority('applicant')")
@SecuredBySpring(value = "Controller", description = "Only pending partner can view the application terms",
        securedType = ProjectTermsController.class)
public class ProjectTermsController {

    @Autowired
    private ProjectTermsModelPopulator projectTermsModelPopulator;

    @Autowired
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;

    @GetMapping
    public String getTerms(@PathVariable long projectId,
                           @PathVariable long organisationId,
                           Model model,
                           @ModelAttribute(name = "form", binding = false) ProjectTermsForm form) {

        ProjectTermsViewModel viewModel = projectTermsModelPopulator.populate(projectId, organisationId);
        model.addAttribute("model", viewModel);

        return "project/pending-partner-progress/terms-and-conditions";
    }

    @PostMapping
    public String acceptTerms(@PathVariable long projectId,
                               @PathVariable long organisationId,
                               Model model,
                               @ModelAttribute(name = "form", binding = false) ProjectTermsForm form,
                               @SuppressWarnings("unused") BindingResult bindingResult,
                               ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> getTerms(projectId, organisationId, model, form);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            RestResult<Void> result = pendingPartnerProgressRestService.markTermsAndConditionsComplete(projectId, organisationId);

            return validationHandler.addAnyErrors(result)
                    .failNowOrSucceedWith(
                            failureView,
                            () -> format("redirect:/project/{projectId}/organisation/{organisationId}/terms-and-conditions#terms-accepted", projectId, organisationId));
        });
    }
}
