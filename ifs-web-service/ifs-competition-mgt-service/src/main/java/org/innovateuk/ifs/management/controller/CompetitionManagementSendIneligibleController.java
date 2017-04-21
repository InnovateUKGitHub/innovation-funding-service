package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.form.InformIneligibleForm;
import org.innovateuk.ifs.management.model.InformIneligibleModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * This controller will handle all Competition Managment requests related to informing #
 * applicants that their application is ineligible
 */
@Controller
@RequestMapping("/competition/application/{applicationId}/ineligible")
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionManagementSendIneligibleController {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private InformIneligibleModelPopulator informIneligibleModelPopulator;

    @GetMapping
    public String getSendIneligible(Model model,
                                    @PathVariable("applicationId") long applicationId,
                                    @ModelAttribute("form") InformIneligibleForm form) {
        ApplicationResource applicationResource = applicationRestService.getApplicationById(applicationId).getSuccessObjectOrThrowException();
        if (applicationResource.getApplicationState() != ApplicationState.INELIGIBLE) {
            return getRedirect(applicationResource);
        }
        model.addAttribute("model", informIneligibleModelPopulator.populateModel(applicationResource));
        return "competition/inform-ineligible";
    }

    @PostMapping("/send")
    public String sendEmail(Model model,
                            @PathVariable("applicationId") long applicationId,
                            @ModelAttribute("form") @Valid InformIneligibleForm form,
                            ValidationHandler validationHandler) {
        ApplicationResource applicationResource = applicationRestService.getApplicationById(applicationId).getSuccessObjectOrThrowException();

        Supplier<String> failureView = () -> getSendIneligible(model, applicationId, form);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> sendResult = applicationRestService.updateApplicationState(applicationId, ApplicationState.INELIGIBLE_INFORMED)
                    .toServiceResult();
            return validationHandler.addAnyErrors(sendResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () -> getRedirect(applicationResource));
        });
    }

    private String getRedirect(ApplicationResource applicationResource) {
        return format("redirect:/competition/%s/applications/ineligible", applicationResource.getCompetition());
    }
}
