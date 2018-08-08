package org.innovateuk.ifs.assessment.profile.controller;

import org.innovateuk.ifs.assessment.profile.form.AssessorProfileEditDetailsForm;
import org.innovateuk.ifs.assessment.profile.populator.AssessorProfileEditDetailsModelPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.populator.AssessorProfileDetailsModelPopulator;
import org.innovateuk.ifs.profile.service.ProfileRestService;
import org.innovateuk.ifs.user.resource.UserProfileResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.function.Supplier;

import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * Controller to manage the editing of the Assessors Profile details page
 */
@Controller
@RequestMapping("/profile/details")
@SecuredBySpring(value = "Controller", description = "Assessors can edit their details", securedType = AssessorProfileDetailsEditController.class)
@PreAuthorize("hasAuthority('assessor')")
public class AssessorProfileDetailsEditController {

    @Autowired
    private AssessorProfileDetailsModelPopulator assessorDetailsModelPopulator;

    @Autowired
    private AssessorProfileEditDetailsModelPopulator assessorEditDetailsModelPopulator;

    @Autowired
    private ProfileRestService profileRestService;

    private static final String FORM_ATTR_NAME = "form";

    @GetMapping
    public String getDetails() {
        return "redirect:/profile/details/skills";
    }

    @GetMapping("/edit")
    public String getDetailsEdit(Model model,
                                 UserResource loggedInUser,
                                 @ModelAttribute(name = FORM_ATTR_NAME, binding = false) AssessorProfileEditDetailsForm form,
                                 BindingResult bindingResult) {
        return doViewEditYourDetails(loggedInUser, model, form, bindingResult);
    }

    @PostMapping("/edit")
    public String submitDetails(Model model,
                                UserResource loggedInUser,
                                @Valid @ModelAttribute(FORM_ATTR_NAME) AssessorProfileEditDetailsForm form,
                                BindingResult bindingResult,
                                ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> doViewEditYourDetails(loggedInUser, model, form, bindingResult);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            UserProfileResource profileDetails = new UserProfileResource();
            profileDetails.setFirstName(form.getFirstName());
            profileDetails.setLastName(form.getLastName());
            profileDetails.setPhoneNumber(form.getPhoneNumber());
            profileDetails.setAddress(form.getAddressForm());
            profileDetails.setEmail(loggedInUser.getEmail());
            ServiceResult<Void> detailsResult = profileRestService.updateUserProfile(loggedInUser.getId(), profileDetails).toServiceResult();

            return validationHandler.addAnyErrors(detailsResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () -> "redirect:/assessor/dashboard");
        });
    }

    private String doViewEditYourDetails(UserResource loggedInUser, Model model, AssessorProfileEditDetailsForm form, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            UserProfileResource profileDetails = profileRestService.getUserProfile(loggedInUser.getId()).getSuccess();
            form.setFirstName(profileDetails.getFirstName());
            form.setLastName(profileDetails.getLastName());
            form.setPhoneNumber(profileDetails.getPhoneNumber());
            form.setAddressForm(profileDetails.getAddress());
        }

        model.addAttribute("model", assessorEditDetailsModelPopulator.populateModel(loggedInUser));
        return "profile/details-edit";
    }
}
