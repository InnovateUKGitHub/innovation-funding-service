package org.innovateuk.ifs.management.ineligible.controller;

import org.innovateuk.ifs.application.resource.ApplicationIneligibleSendResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.ineligible.form.InformIneligibleForm;
import org.innovateuk.ifs.management.ineligible.populator.InformIneligibleModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.application.resource.ApplicationState.INELIGIBLE;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * This controller will handle all Competition Managment requests related to informing #
 * applicants that their application is ineligible
 */
@Controller
@RequestMapping("/competition/application/{applicationId}/ineligible")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = CompetitionManagementSendIneligibleController.class)
@PreAuthorize("hasAnyAuthority('comp_admin')")
public class CompetitionManagementSendIneligibleController {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private InformIneligibleModelPopulator informIneligibleModelPopulator;

    @GetMapping
    public String getSendIneligible(Model model,
                                    @PathVariable("applicationId") long applicationId,
                                    @ModelAttribute("form") InformIneligibleForm form) {
        ApplicationResource applicationResource = applicationRestService.getApplicationById(applicationId).getSuccess();
        if (applicationResource.getApplicationState() != INELIGIBLE) {
            return getRedirect(applicationResource);
        }
        model.addAttribute("model", informIneligibleModelPopulator.populateModel(applicationResource, form));
        return "competition/inform-ineligible";
    }

    @PostMapping("/send")
    public String sendEmail(Model model,
                            @PathVariable("applicationId") long applicationId,
                            @ModelAttribute("form") @Valid InformIneligibleForm form,
                            BindingResult result,
                            ValidationHandler validationHandler) {
        ApplicationResource applicationResource = applicationRestService.getApplicationById(applicationId).getSuccess();

        Supplier<String> failureView = () -> getSendIneligible(model, applicationId, form);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> sendResult = applicationRestService.informIneligible(applicationId,
                    new ApplicationIneligibleSendResource(form.getSubject(), form.getMessage()))
                    .toServiceResult();
            return validationHandler.addAnyErrors(sendResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () -> getRedirect(applicationResource));
        });
    }

    private String getRedirect(ApplicationResource applicationResource) {
        return format("redirect:/competition/%s/applications/ineligible", applicationResource.getCompetition());
    }
}
