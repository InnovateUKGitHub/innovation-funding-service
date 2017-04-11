package org.innovateuk.ifs.application;

import org.innovateuk.ifs.application.form.InnovationAreaForm;
import org.innovateuk.ifs.application.populator.ApplicationInnovationAreaPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationInnovationAreaRestService;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.viewmodel.InnovationAreaViewModel;
import org.innovateuk.ifs.commons.error.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.function.Supplier;

/**
 * This controller handles requests by Applicants to change the Innovation Area choice for an Application.
 */
@Controller
@RequestMapping(ApplicationFormController.APPLICATION_BASE_URL+"{applicationId}/form/question/{questionId}/innovation-area")
@PreAuthorize("hasAuthority('applicant')")
public class InnovationAreaController {
    private static String APPLICATION_SAVED_MESSAGE = "applicationSaved";

    @Autowired
    private ApplicationInnovationAreaPopulator innovationAreaPopulator;

    @Autowired
    private ApplicationInnovationAreaRestService applicationInnovationAreaRestService;

    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationDetailsEditableValidator applicationDetailsEditableValidator;

    @GetMapping
    public String getInnovationAreas(Model model, @PathVariable("applicationId") Long applicationId, @PathVariable("questionId") Long questionId) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);

        checkIfAllowed(questionId, applicationResource);

        InnovationAreaViewModel innovationAreaViewModel = innovationAreaPopulator.populate(applicationResource, questionId);

        model.addAttribute("model", innovationAreaViewModel);
        model.addAttribute("form", new InnovationAreaForm());

        return "application/innovation-areas";
    }

    @PostMapping
    public String submitInnovationAreaChoice(@Valid @ModelAttribute("form") InnovationAreaForm innovationAreaForm, BindingResult bindingResult, HttpServletResponse response,
                                             ValidationHandler validationHandler, Model model, @PathVariable Long applicationId, @PathVariable Long questionId) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);

        checkIfAllowed(questionId, applicationResource);

        InnovationAreaViewModel innovationAreaViewModel = innovationAreaPopulator.populate(applicationResource, questionId);

        model.addAttribute("model", innovationAreaViewModel);

        Supplier<String> failureView = () -> "application/innovation-areas";

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            return validationHandler.addAnyErrors(saveInnovationAreaChoice(applicationId, innovationAreaForm)).failNowOrSucceedWith(failureView,
                    () -> {cookieFlashMessageFilter.setFlashMessage(response, APPLICATION_SAVED_MESSAGE);
                    return "redirect:/application/"+applicationId+"/form/question/"+questionId;
            });
        });
    }


    private RestResult<ApplicationResource> saveInnovationAreaChoice(Long applicationId, InnovationAreaForm innovationAreaForm) {
        if(innovationAreaForm.getInnovationAreaChoice().equals("NOT_APPLICABLE")) {
            return applicationInnovationAreaRestService.setApplicationInnovationAreaToNotApplicable(applicationId);
        }
        else {
            Long innovationAreaId = Long.valueOf(innovationAreaForm.getInnovationAreaChoice());
            return applicationInnovationAreaRestService.saveApplicationInnovationAreaChoice(applicationId, innovationAreaId);
        }
    }

    private void checkIfAllowed(Long questionId, ApplicationResource applicationResource) throws ForbiddenActionException {
        if(!applicationDetailsEditableValidator.questionAndApplicationHaveAllowedState(questionId, applicationResource)) {
            throw new ForbiddenActionException();
        }
    }
}
