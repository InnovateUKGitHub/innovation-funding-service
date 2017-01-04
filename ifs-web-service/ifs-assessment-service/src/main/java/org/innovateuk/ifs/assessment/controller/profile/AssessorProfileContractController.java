package org.innovateuk.ifs.assessment.controller.profile;

import org.innovateuk.ifs.assessment.form.profile.AssessorProfileContractForm;
import org.innovateuk.ifs.assessment.model.profile.AssessorProfileContractAnnexModelPopulator;
import org.innovateuk.ifs.assessment.model.profile.AssessorProfileContractModelPopulator;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.user.resource.ProfileContractResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
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

import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * Controller to manage the Assessor Profile Contract page
 */
@Controller
@RequestMapping("/profile/contract")
public class AssessorProfileContractController {

    @Autowired
    private AssessorProfileContractModelPopulator assessorProfileContractModelPopulator;

    @Autowired
    private AssessorProfileContractAnnexModelPopulator assessorProfileContractAnnexModelPopulator;

    @Autowired
    private UserService userService;

    public enum ContractAnnexParameter {
        A,
        B,
        C
    }

    private static final String FORM_ATTR_NAME = "form";

    @RequestMapping(method = RequestMethod.GET)
    public String getContract(Model model,
                              @ModelAttribute("loggedInUser") UserResource loggedInUser,
                              @ModelAttribute(FORM_ATTR_NAME) AssessorProfileContractForm form) {
        ProfileContractResource profileContract = userService.getProfileContract(loggedInUser.getId());
        populateFormWithExistingValues(form, profileContract);
        return doViewContract(model, profileContract);
    }

    @RequestMapping(value = "/annex/{annex}", method = RequestMethod.GET)
    public String getAnnex(Model model, @PathVariable("annex") ContractAnnexParameter annex) {
        model.addAttribute("model", assessorProfileContractAnnexModelPopulator.populateModel(annex));
        return "profile/annex";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitContract(Model model,
                              @ModelAttribute("loggedInUser") UserResource loggedInUser,
                              @Valid @ModelAttribute(FORM_ATTR_NAME) AssessorProfileContractForm form,
                              @SuppressWarnings("unused") BindingResult bindingResult,
                              ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> {
            ProfileContractResource profileContract = userService.getProfileContract(loggedInUser.getId());
            return doViewContract(model, profileContract);
        };

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> updateResult = userService.updateProfileContract(loggedInUser.getId());
            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () -> "redirect:/assessor/dashboard");
        });
    }

    private String doViewContract(Model model, ProfileContractResource profileContract) {
        model.addAttribute("model", assessorProfileContractModelPopulator.populateModel(profileContract));
        return "profile/contract";
    }

    private void populateFormWithExistingValues(AssessorProfileContractForm form, ProfileContractResource profileContract) {
        form.setAgreesToTerms(profileContract.isCurrentAgreement());
    }
}
