package com.worth.ifs.assessment.controller.profile;

import com.worth.ifs.assessment.form.profile.AssessorProfileEditDetailsForm;
import com.worth.ifs.assessment.model.profile.AssessorProfileDetailsModelPopulator;
import com.worth.ifs.assessment.model.profile.AssessorProfileEditDetailsModelPopulator;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.invite.service.EthnicityRestService;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
 * Controller to manage the Assessor Profile Skills page
 */
@Controller
@RequestMapping("/profile")
public class AssessorProfileDetailsController {

    @Autowired
    private AssessorProfileDetailsModelPopulator assessorDetailsModelPopulator;

    @Autowired
    private AssessorProfileEditDetailsModelPopulator assessorEditDetailsModelPopulator;

    @Autowired
    private UserService userService;

    @Autowired
    private EthnicityRestService ethnicityRestService;


    private static final String FORM_ATTR_NAME = "form";

    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public String getDetails(Model model,
                            @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        return doViewYourDetails(loggedInUser, model);
    }

    @RequestMapping(value = "/details-edit", method = RequestMethod.GET)
    public String getDetailsEdit(Model model,
                            @ModelAttribute("loggedInUser") UserResource loggedInUser,
                            @ModelAttribute(FORM_ATTR_NAME) AssessorProfileEditDetailsForm form,
                            BindingResult bindingResult) {
        return doViewEditYourDetails(loggedInUser, model, form, bindingResult);
    }

    @RequestMapping(value = "/details-edit", method = RequestMethod.POST)
    public String submitDetails(Model model,
                               @ModelAttribute("loggedInUser") UserResource loggedInUser,
                               @Valid @ModelAttribute(FORM_ATTR_NAME) AssessorProfileEditDetailsForm form,
                               BindingResult bindingResult,
                               ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> doViewEditYourDetails(loggedInUser, model, form, bindingResult);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            RestResult<UserResource> result = userService.updateDetails(loggedInUser.getId(), loggedInUser.getEmail(), form.getFirstName(), form.getLastName(), form.getTitle(),form.getPhoneNumber());
            return validationHandler.addAnyErrors(result, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> "redirect:/assessor/dashboard");
        });
    }

    private String doViewYourDetails(UserResource loggedInUser, Model model) {
        model.addAttribute("model", assessorDetailsModelPopulator.populateModel(loggedInUser));
        return "profile/details";
    }

    private String doViewEditYourDetails(UserResource loggedInUser, Model model, AssessorProfileEditDetailsForm form, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            populateFormWithExistingValues(loggedInUser, form);
        }
        model.addAttribute("model", assessorEditDetailsModelPopulator.populateModel(loggedInUser.getEmail()));
        return "profile/details-edit";
    }

    private void populateFormWithExistingValues(UserResource loggedInUser, AssessorProfileEditDetailsForm form) {
        form.setTitle(loggedInUser.getTitle());
        form.setFirstName(loggedInUser.getFirstName());
        form.setLastName(loggedInUser.getLastName());
        form.setGender(loggedInUser.getGender());
        form.setPhoneNumber(loggedInUser.getPhoneNumber());
        //form.setEthnicity(loggedInUser.getEthnicity());
        form.setDisability(loggedInUser.getDisability());
    }
}

