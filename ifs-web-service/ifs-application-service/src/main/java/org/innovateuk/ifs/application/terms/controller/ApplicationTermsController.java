package org.innovateuk.ifs.application.terms.controller;

import org.innovateuk.ifs.application.common.populator.ApplicationTermsModelPopulator;
import org.innovateuk.ifs.application.common.populator.ApplicationTermsPartnerModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.application.terms.form.ApplicationTermsForm;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/terms-and-conditions")
@PreAuthorize("hasAuthority('applicant')")
@SecuredBySpring(value = "Controller",
        description = "Only applicants are allowed to view the application terms",
        securedType = ApplicationTermsController.class)
public class ApplicationTermsController {

    private UserRestService userRestService;
    private ApplicationTermsModelPopulator applicationTermsModelPopulator;
    private QuestionStatusRestService questionStatusRestService;
    private ApplicationRestService applicationRestService;
    private ApplicationTermsPartnerModelPopulator applicationTermsPartnerModelPopulator;


    public ApplicationTermsController(UserRestService userRestService,
                                      QuestionStatusRestService questionStatusRestService,
                                      ApplicationRestService applicationRestService,
                                      ApplicationTermsPartnerModelPopulator applicationTermsPartnerModelPopulator,
                                      ApplicationTermsModelPopulator applicationTermsModelPopulator) {
        this.userRestService = userRestService;
        this.questionStatusRestService = questionStatusRestService;
        this.applicationRestService = applicationRestService;
        this.applicationTermsModelPopulator = applicationTermsModelPopulator;
        this.applicationTermsPartnerModelPopulator = applicationTermsPartnerModelPopulator;
    }

    @GetMapping
    public String getTerms(@PathVariable long applicationId,
                           @PathVariable long questionId,
                           UserResource user,
                           Model model,
                           @ModelAttribute(name = "form", binding = false) ApplicationTermsForm form) {
        model.addAttribute("model", applicationTermsModelPopulator.populate(user, applicationId, questionId));
        return "application/terms-and-conditions";
    }

    @PostMapping
    public String acceptTerms(@PathVariable long applicationId,
                              @PathVariable long questionId,
                              UserResource user,
                              Model model,
                              @Valid @ModelAttribute(name = "form") ApplicationTermsForm form,
                              @SuppressWarnings("unused") BindingResult bindingResult,
                              ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> getTerms(applicationId, questionId, user, model, form);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ProcessRoleResource processRole = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
            RestResult<List<ValidationMessages>> result = questionStatusRestService.markAsComplete(questionId, applicationId, processRole.getId());

            return validationHandler.addAnyErrors(result, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(
                            failureView,
                            () -> format("redirect:%s%d/form/question/%d/terms-and-conditions#terms-accepted", APPLICATION_BASE_URL, applicationId, questionId));
        });
    }

    @GetMapping("/partner-status")
    public String getPartnerStatus(@PathVariable long applicationId, @PathVariable long questionId, Model model) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        if (!application.isOpen()) {
            throw new ForbiddenActionException("Cannot view partners on a non-open application");
        } else if (!application.isCollaborativeProject()) {
            throw new ForbiddenActionException("Cannot view partners on a non-collaborative application");
        }

        model.addAttribute("model", applicationTermsPartnerModelPopulator.populate(application, questionId));
        return "application/terms-and-conditions-partner-status";
    }
}