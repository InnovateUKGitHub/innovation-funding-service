package org.innovateuk.ifs.assessment.controller.profile;

import org.innovateuk.ifs.assessment.form.profile.AssessorProfileAgreementForm;
import org.innovateuk.ifs.assessment.model.profile.AssessorProfileAgreementModelPopulator;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.user.resource.ProfileAgreementResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.function.Supplier;

import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * Controller to manage the Assessor Profile Agreement view.
 */
@Controller
@RequestMapping("/profile/agreement")
@PreAuthorize("hasAuthority('assessor')")
public class AssessorProfileAgreementController {

    @Autowired
    private AssessorProfileAgreementModelPopulator assessorProfileAgreementModelPopulator;

    @Autowired
    private UserService userService;

    private static final String FORM_ATTR_NAME = "form";

    @RequestMapping(method = RequestMethod.GET)
    public String getAgreement(Model model,
                               @ModelAttribute("loggedInUser") UserResource loggedInUser,
                               @ModelAttribute(FORM_ATTR_NAME) AssessorProfileAgreementForm form) {
        ProfileAgreementResource profileAgreementResource = userService.getProfileAgreement(loggedInUser.getId());
        populateFormWithExistingValues(form, profileAgreementResource);
        return doViewAgreement(model, profileAgreementResource);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitAgreement(Model model,
                                  @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                  @Valid @ModelAttribute(FORM_ATTR_NAME) AssessorProfileAgreementForm form,
                                  @SuppressWarnings("unused") BindingResult bindingResult,
                                  ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> {
            ProfileAgreementResource profileAgreementResource = userService.getProfileAgreement(loggedInUser.getId());
            return doViewAgreement(model, profileAgreementResource);
        };

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> updateResult = userService.updateProfileAgreement(loggedInUser.getId());
            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () -> "redirect:/assessor/dashboard");
        });
    }

    private String doViewAgreement(Model model, ProfileAgreementResource profileAgreementResource) {
        model.addAttribute("model", assessorProfileAgreementModelPopulator.populateModel(profileAgreementResource));
        return "profile/agreement";
    }

    private void populateFormWithExistingValues(AssessorProfileAgreementForm form, ProfileAgreementResource profileAgreementResource) {
        form.setAgreesToTerms(profileAgreementResource.isCurrentAgreement());
    }
}
