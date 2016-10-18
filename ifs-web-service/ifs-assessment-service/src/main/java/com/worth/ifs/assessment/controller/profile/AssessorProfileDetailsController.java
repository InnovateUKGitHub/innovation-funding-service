package com.worth.ifs.assessment.controller.profile;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.service.AddressRestService;
import com.worth.ifs.assessment.controller.AssessorRegistrationController;
import com.worth.ifs.assessment.form.profile.AssessorProfileEditDetailsForm;
import com.worth.ifs.assessment.form.registration.AssessorRegistrationForm;
import com.worth.ifs.assessment.model.profile.AssessorProfileDetailsModelPopulator;
import com.worth.ifs.assessment.model.profile.AssessorProfileEditDetailsModelPopulator;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.form.AddressForm;
import com.worth.ifs.invite.service.EthnicityRestService;
import com.worth.ifs.user.resource.EthnicityResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
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
    private AddressRestService addressRestService;

    @Autowired
    private UserService userService;

    @Autowired
    private EthnicityRestService ethnicityRestService;

    private List<EthnicityResource> ethnicityOptions;

    private static final String FORM_ATTR_NAME = "form";

    private static final Log LOG = LogFactory.getLog(AssessorProfileDetailsController.class);

    private Validator validator;

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

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

        addAddressOptions(form);
        addSelectedAddress(form);
        validateAddressForm(form, bindingResult);

        Supplier<String> failureView = () -> doViewEditYourDetails(loggedInUser, model, form, bindingResult);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            loggedInUser.setTitle(form.getTitle());
            loggedInUser.setFirstName(form.getFirstName());
            loggedInUser.setLastName(form.getLastName());
            loggedInUser.setEthnicity(form.getEthnicity().getId());
            loggedInUser.setGender(form.getGender());
            loggedInUser.setDisability(form.getDisability());
            loggedInUser.setPhoneNumber(form.getPhoneNumber());
            RestResult<UserResource> result = userService.updateDetails(loggedInUser);
            return validationHandler.addAnyErrors(result, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> "redirect:/assessor/dashboard");
        });
    }

    @RequestMapping(value = "/details-edit", params = "manual-address", method = RequestMethod.POST)
    public String manualAddress(Model model,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                @ModelAttribute(FORM_ATTR_NAME) AssessorProfileEditDetailsForm form,
                                BindingResult bindingResult) {
        form.setAddressForm(new AddressForm());
        form.getAddressForm().setManualAddress(true);

        return doViewEditYourDetails(loggedInUser, model, form, bindingResult);
    }

    @RequestMapping(value = "/details-edit", params = "search-address", method = RequestMethod.POST)
    public String searchAddress(Model model,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                @ModelAttribute(FORM_ATTR_NAME) AssessorProfileEditDetailsForm form,
                                BindingResult bindingResult, ValidationHandler validationHandler) {

        Supplier<String> view = () -> doViewEditYourDetails(loggedInUser, model, form, bindingResult);

        addAddressOptions(form);
        form.getAddressForm().setTriedToSearch(true);

        if (form.getAddressForm().getPostcodeInput().isEmpty()) {
            bindingResult.rejectValue("addressForm.postcodeInput", "validation.standard.postcode.required");
        }

        return validationHandler.failNowOrSucceedWith(view, view);
    }

    @RequestMapping(value = "/details-edit", params = "select-address", method = RequestMethod.POST)
    public String selectAddress(Model model,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                @ModelAttribute(FORM_ATTR_NAME) AssessorProfileEditDetailsForm form,
                                BindingResult bindingResult) {
        addAddressOptions(form);
        addSelectedAddress(form);

        return doViewEditYourDetails(loggedInUser, model, form, bindingResult);
    }

    private void validateAddressForm(AssessorProfileEditDetailsForm form, BindingResult bindingResult) {
        if (postcodeIsSelected(form)) {
            validator.validate(form.getAddressForm().getSelectedPostcode(), bindingResult);
        } else {
            FieldError fieldError = new FieldError("address", "address", "Please enter your address details");
            bindingResult.addError(fieldError);
        }
        form.getAddressForm().setTriedToSave(true);
    }

    private boolean postcodeIsSelected(AssessorProfileEditDetailsForm assessorRegistrationForm) {
        if (assessorRegistrationForm.getAddressForm() == null) {
            return false;
        }
        return assessorRegistrationForm.getAddressForm().getSelectedPostcode() != null;
    }

    private void addAddressOptions(AssessorProfileEditDetailsForm registrationForm) {
        if (StringUtils.hasText(registrationForm.getAddressForm().getPostcodeInput())) {
            AddressForm addressForm = registrationForm.getAddressForm();
            addressForm.setPostcodeOptions(searchPostcode(registrationForm.getAddressForm().getPostcodeInput()));
            addressForm.setPostcodeInput(registrationForm.getAddressForm().getPostcodeInput());
            registrationForm.setAddressForm(addressForm);
        }
    }

    private void addSelectedAddress(AssessorProfileEditDetailsForm registrationForm) {
        AddressForm addressForm = registrationForm.getAddressForm();
        if (StringUtils.hasText(addressForm.getSelectedPostcodeIndex())) {
            try {
                AddressResource selectedAddress = new AddressResource();
                selectedAddress = addressForm.getPostcodeOptions().get(
                        Integer.parseInt(
                                addressForm.getSelectedPostcodeIndex()));
                addressForm.setSelectedPostcode(selectedAddress);
            } catch (IndexOutOfBoundsException e) {
                LOG.info(e);
            }
        }
    }

    private List<AddressResource> searchPostcode(String postcodeInput) {
        RestResult<List<AddressResource>> addressLookupRestResult =
                addressRestService.doLookup(postcodeInput);
        List<AddressResource> addressResourceList = addressLookupRestResult.handleSuccessOrFailure(
                failure -> new ArrayList<>(),
                addresses -> addresses);
        return addressResourceList;
    }

    private String doViewYourDetails(UserResource loggedInUser, Model model) {
        retrieveEthnicityOptions();
        model.addAttribute("model", assessorDetailsModelPopulator.populateModel(loggedInUser, getEthnicity(loggedInUser.getEthnicity())));
        return "profile/details";
    }

    private String doViewEditYourDetails(UserResource loggedInUser, Model model, AssessorProfileEditDetailsForm form, BindingResult bindingResult) {
        retrieveEthnicityOptions();
        if (!bindingResult.hasErrors()) {
            populateFormWithExistingValues(loggedInUser, form);
        }
        model.addAttribute("ethnicityOptions", ethnicityOptions);
        model.addAttribute("model", assessorEditDetailsModelPopulator.populateModel(loggedInUser.getEmail()));
        return "profile/details-edit";
    }

    private void populateFormWithExistingValues(UserResource loggedInUser, AssessorProfileEditDetailsForm form) {
        form.setTitle(loggedInUser.getTitle());
        form.setFirstName(loggedInUser.getFirstName());
        form.setLastName(loggedInUser.getLastName());
        form.setGender(loggedInUser.getGender());
        form.setEthnicity(getEthnicity(loggedInUser.getEthnicity()));
        form.setDisability(loggedInUser.getDisability());
        form.setPhoneNumber(loggedInUser.getPhoneNumber());
    }

    private void retrieveEthnicityOptions() {
        this.ethnicityOptions = ethnicityRestService.findAllActive().getSuccessObjectOrThrowException();
    }

    private EthnicityResource getEthnicity(Long ethnicityId ) {
        return ethnicityOptions.stream()
                .filter(option -> option.getId().equals(ethnicityId))
                .findAny()
                .orElse(null);
    }
}

