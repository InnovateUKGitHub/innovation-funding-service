package com.worth.ifs.assessment.controller.profile;

import com.worth.ifs.assessment.form.profile.AssessorProfileTermsForm;
import com.worth.ifs.assessment.model.profile.AssessorProfileTermsAnnexModelPopulator;
import com.worth.ifs.assessment.model.profile.AssessorProfileTermsModelPopulator;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.user.resource.ProfileContractResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.function.Supplier;

import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * Controller to manage the Assessor Profile Terms of Contract page
 */
@Controller
@RequestMapping("/profile/terms")
public class AssessorProfileTermsController {

    @Autowired
    private AssessorProfileTermsModelPopulator assessorProfileTermsModelPopulator;

    @Autowired
    private AssessorProfileTermsAnnexModelPopulator assessorProfileTermsAnnexModelPopulator;

    @Autowired
    private UserService userService;

    public enum ContractAnnexParameter {
        A,
        B,
        C
    }

    private static final String FORM_ATTR_NAME = "form";

    @RequestMapping(method = RequestMethod.GET)
    public String getTerms(Model model,
                           @ModelAttribute("loggedInUser") UserResource loggedInUser,
                           @ModelAttribute(FORM_ATTR_NAME) AssessorProfileTermsForm form) {
        ProfileContractResource profileContract = userService.getProfileContract(loggedInUser.getId());
        populateFormWithExistingValues(form, profileContract);
        return doViewTerms(model, profileContract);
    }

    @RequestMapping(value = "/annex/{annex}", method = RequestMethod.GET)
    public String getAnnex(Model model, @PathVariable("annex") ContractAnnexParameter annex) {
        model.addAttribute("model", assessorProfileTermsAnnexModelPopulator.populateModel(annex));
        return "profile/annex";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitTerms(Model model,
                              @ModelAttribute("loggedInUser") UserResource loggedInUser,
                              @Valid @ModelAttribute(FORM_ATTR_NAME) AssessorProfileTermsForm form,
                              @SuppressWarnings("unused") BindingResult bindingResult,
                              ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> {
            ProfileContractResource profileContract = userService.getProfileContract(loggedInUser.getId());
            return doViewTerms(model, profileContract);
        };

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> updateResult = userService.updateProfileContract(loggedInUser.getId());
            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () -> "redirect:/assessor/dashboard");
        });
    }

    private String doViewTerms(Model model, ProfileContractResource profileContract) {
        model.addAttribute("model", assessorProfileTermsModelPopulator.populateModel(profileContract));
        return "profile/terms";
    }

    private void populateFormWithExistingValues(AssessorProfileTermsForm form, ProfileContractResource profileContract) {
        form.setAgreesToTerms(profileContract.isCurrentAgreement());
    }
}