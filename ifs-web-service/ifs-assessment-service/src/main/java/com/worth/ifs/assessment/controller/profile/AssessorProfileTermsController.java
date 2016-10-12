package com.worth.ifs.assessment.controller.profile;

import com.worth.ifs.assessment.form.profile.AssessorProfileTermsForm;
import com.worth.ifs.assessment.model.profile.AssessorProfileTermsModelPopulator;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.user.resource.ContractResource;
import com.worth.ifs.user.resource.ProfileResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.ContractRestService;
import com.worth.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    private ContractRestService contractRestService;

    @Autowired
    private UserService userService;

    private static final String FORM_ATTR_NAME = "form";

    @RequestMapping(method = RequestMethod.GET)
    public String getTerms(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorProfileTermsForm form) {
        return doViewTerms(model);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitTerms(Model model,
                              @ModelAttribute("loggedInUser") UserResource loggedInUser,
                              @Valid @ModelAttribute(FORM_ATTR_NAME) AssessorProfileTermsForm form,
                              @SuppressWarnings("unused") BindingResult bindingResult,
                              ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> doViewTerms(model);

        model.addAttribute("model", assessorProfileTermsModelPopulator.populateModel());

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ProfileResource profile = new ProfileResource();

            RestResult<ContractResource> contractResult = contractRestService.getCurrentContract();

            if(!contractResult.isSuccess()) {
                return validationHandler.addAnyErrors(contractResult, fieldErrorsToFieldErrors(), asGlobalErrors()).
                        failNowOrSucceedWith(failureView, () -> "redirect:/profile/terms");
            }

            profile.setContract(contractResult.getSuccessObjectOrThrowException());
            ServiceResult<Void> userResult = userService.updateUserContract(loggedInUser.getId(), profile);
            return validationHandler.addAnyErrors(userResult, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> "redirect:/profile/terms");
        });
    }

    private String doViewTerms(Model model) {
        model.addAttribute("model", assessorProfileTermsModelPopulator.populateModel());
        return "profile/terms";
    }
}