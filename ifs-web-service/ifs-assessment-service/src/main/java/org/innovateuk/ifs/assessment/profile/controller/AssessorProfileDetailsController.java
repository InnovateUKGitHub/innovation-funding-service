package org.innovateuk.ifs.assessment.profile.controller;

import org.innovateuk.ifs.assessment.profile.form.AssessorProfileEditDetailsForm;
import org.innovateuk.ifs.assessment.profile.populator.AssessorProfileDetailsModelPopulator;
import org.innovateuk.ifs.assessment.profile.populator.AssessorProfileEditDetailsModelPopulator;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.service.EthnicityRestService;
import org.innovateuk.ifs.profile.service.ProfileRestService;
import org.innovateuk.ifs.user.resource.EthnicityResource;
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
import java.util.List;
import java.util.function.Supplier;

import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * Controller to manage the Assessor Profile Skills page
 */
@Controller
@RequestMapping("/profile/details")
@PreAuthorize("hasAuthority('assessor')")
public class AssessorProfileDetailsController {

    @Autowired
    private AssessorProfileDetailsModelPopulator assessorDetailsModelPopulator;

    @Autowired
    private AssessorProfileEditDetailsModelPopulator assessorEditDetailsModelPopulator;

    @Autowired
    private ProfileRestService profileRestService;

    @Autowired
    private EthnicityRestService ethnicityRestService;

    private static final String FORM_ATTR_NAME = "form";

    @GetMapping
    public String getDetails(Model model,
                             @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        return doViewYourDetails(loggedInUser, model);
    }

    @GetMapping("/edit")
    public String getDetailsEdit(Model model,
                                 @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                 @ModelAttribute(FORM_ATTR_NAME) AssessorProfileEditDetailsForm form,
                                 BindingResult bindingResult) {
        return doViewEditYourDetails(loggedInUser, model, form, bindingResult);
    }

    @PostMapping("/edit")
    public String submitDetails(Model model,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                @Valid @ModelAttribute(FORM_ATTR_NAME) AssessorProfileEditDetailsForm form,
                                BindingResult bindingResult,
                                ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> doViewEditYourDetails(loggedInUser, model, form, bindingResult);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            UserProfileResource profileDetails = new UserProfileResource();
            profileDetails.setFirstName(form.getFirstName());
            profileDetails.setLastName(form.getLastName());
            profileDetails.setEthnicity(form.getEthnicity());
            profileDetails.setGender(form.getGender());
            profileDetails.setDisability(form.getDisability());
            profileDetails.setPhoneNumber(form.getPhoneNumber());
            profileDetails.setAddress(form.getAddressForm());
            profileDetails.setEmail(loggedInUser.getEmail());
            ServiceResult<Void> detailsResult = profileRestService.updateUserProfile(loggedInUser.getId(), profileDetails).toServiceResult();

            return validationHandler.addAnyErrors(detailsResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () -> "redirect:/assessor/dashboard");
        });
    }

    private String doViewYourDetails(UserResource loggedInUser, Model model) {
        model.addAttribute("model", assessorDetailsModelPopulator.populateModel(loggedInUser));
        return "profile/details";
    }

    private String doViewEditYourDetails(UserResource loggedInUser, Model model, AssessorProfileEditDetailsForm form, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            UserProfileResource profileDetails = profileRestService.getUserProfile(loggedInUser.getId()).getSuccessObjectOrThrowException();
            form.setFirstName(profileDetails.getFirstName());
            form.setLastName(profileDetails.getLastName());
            form.setPhoneNumber(profileDetails.getPhoneNumber());
            form.setAddressForm(profileDetails.getAddress());
        }

        model.addAttribute("ethnicityOptions", getEthnicityOptions());
        model.addAttribute("model", assessorEditDetailsModelPopulator.populateModel(loggedInUser));
        return "profile/details-edit";
    }

    private List<EthnicityResource> getEthnicityOptions() {
        return ethnicityRestService.findAllActive().getSuccessObjectOrThrowException();
    }
}
