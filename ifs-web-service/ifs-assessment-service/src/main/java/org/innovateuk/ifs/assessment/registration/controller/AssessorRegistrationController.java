package org.innovateuk.ifs.assessment.registration.controller;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.service.AddressRestService;
import org.innovateuk.ifs.assessment.registration.form.AssessorRegistrationForm;
import org.innovateuk.ifs.assessment.registration.populator.AssessorRegistrationBecomeAnAssessorModelPopulator;
import org.innovateuk.ifs.assessment.registration.populator.AssessorRegistrationModelPopulator;
import org.innovateuk.ifs.assessment.registration.service.AssessorService;
import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.form.AddressForm;
import org.innovateuk.ifs.invite.service.EthnicityRestService;
import org.innovateuk.ifs.user.resource.EthnicityResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static java.lang.String.format;

/**
 * Controller to manage Assessor Registration.
 */
@Controller
@RequestMapping("/registration")
@PreAuthorize("permitAll")
public class AssessorRegistrationController {
    private static final Log LOG = LogFactory.getLog(AssessorRegistrationController.class);

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private AddressRestService addressRestService;

    @Autowired
    private AssessorService assessorService;

    @Autowired
    private EthnicityRestService ethnicityRestService;

    @Autowired
    private AssessorRegistrationBecomeAnAssessorModelPopulator becomeAnAssessorModelPopulator;

    @Autowired
    private AssessorRegistrationModelPopulator yourDetailsModelPopulator;

    @Autowired
    private CompetitionInviteRestService compeitionInviteRestService;

    @Autowired
    @Qualifier("mvcValidator")
    private Validator validator;

    @GetMapping("/{inviteHash}/start")
    public String becomeAnAssessor(Model model,
                                   @PathVariable("inviteHash") String inviteHash) {

        model.addAttribute("model", becomeAnAssessorModelPopulator.populateModel(inviteHash));
        return "registration/become-assessor";
    }

    @GetMapping("/{inviteHash}/register")
    public String yourDetails(Model model,
                              @PathVariable("inviteHash") String inviteHash,
                              @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationForm form) {
        return doViewYourDetails(model, inviteHash);
    }

    @PostMapping("/{inviteHash}/register")
    public String submitYourDetails(Model model,
                                    @PathVariable("inviteHash") String inviteHash,
                                    @Valid @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationForm registrationForm,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler) {

        addAddressOptions(registrationForm);
        validateAddressForm(registrationForm, bindingResult);

        Supplier<String> failureView = () -> doViewYourDetails(model, inviteHash);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> result = assessorService.createAssessorByInviteHash(inviteHash, registrationForm);

            result.getErrors().forEach(error -> {
                if (StringUtils.hasText(error.getFieldName())) {
                    bindingResult.rejectValue(error.getFieldName(), "registration." + error.getErrorKey());
                } else {
                    bindingResult.reject("registration." + error.getErrorKey());
                }
            });

            return validationHandler.
                    failNowOrSucceedWith(failureView, () -> format("redirect:/registration/%s/register/account-created", inviteHash));
        });
    }

    @GetMapping(value = "/{inviteHash}/register/account-created")
    public String accountCreated(Model model, @PathVariable("inviteHash") String inviteHash, @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        boolean userIsLoggedIn = loggedInUser != null;

        // the user is already logged in, take them back to the invite
        if (userIsLoggedIn) {
            return format("redirect:/invite/competition/%s", inviteHash);
        }

        return compeitionInviteRestService.checkExistingUser(inviteHash).andOnSuccessReturn(userExists -> {
            if (!userExists) {
                // reached here without creating an assessor, redirect back to the invite
                return format("redirect:/invite/competition/%s", inviteHash);
            }
            else {
                model.addAttribute("competitionInviteHash", inviteHash);
                return "registration/account-created";
            }
        }).getSuccessObjectOrThrowException();
    }

    @PostMapping(value = "/{inviteHash}/register", params = "manual-address")
    public String manualAddress(Model model,
                                @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationForm registrationForm,
                                @PathVariable("inviteHash") String inviteHash) {
        registrationForm.setAddressForm(new AddressForm());
        registrationForm.getAddressForm().setManualAddress(true);

        return doViewYourDetails(model, inviteHash);
    }

    @PostMapping(value = "/{inviteHash}/register", params = "search-address")
    public String searchAddress(Model model,
                                @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationForm registrationForm,
                                @PathVariable("inviteHash") String inviteHash,
                                BindingResult bindingResult, ValidationHandler validationHandler) {

        Supplier<String> view = () -> doViewYourDetails(model, inviteHash);

        addAddressOptions(registrationForm);
        registrationForm.getAddressForm().setTriedToSearch(true);
        registrationForm.getAddressForm().setSelectedPostcodeIndex(null);

        if (registrationForm.getAddressForm().getPostcodeInput().isEmpty()) {
            bindingResult.rejectValue("addressForm.postcodeInput", "validation.standard.postcodesearch.required");
        }

        return validationHandler.failNowOrSucceedWith(view, view);
    }

    @PostMapping(value = "/{inviteHash}/register", params = "select-address")
    public String selectAddress(Model model,
                                @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationForm registrationForm,
                                @PathVariable("inviteHash") String inviteHash) {
        addAddressOptions(registrationForm);
        addSelectedAddress(registrationForm);

        return doViewYourDetails(model, inviteHash);
    }

    private void validateAddressForm(AssessorRegistrationForm assessorRegistrationForm, BindingResult bindingResult) {
        if (postcodeIsSelected(assessorRegistrationForm)) {
            ValidationUtils.invokeValidator(validator, assessorRegistrationForm.getAddressForm().getSelectedPostcode(), bindingResult);
        } else {
            if (postcodeIndexIsSubmitted(assessorRegistrationForm)) {
                bindingResult.rejectValue("addressForm.postcodeOptions", "validation.standard.postcodeoptions.required");
                assessorRegistrationForm.getAddressForm().setSelectedPostcodeIndex(null);
            } else {
                bindingResult.rejectValue("addressForm.postcodeInput", "validation.standard.postcodesearch.required");
                assessorRegistrationForm.getAddressForm().setTriedToSearch(true);
                assessorRegistrationForm.getAddressForm().setTriedToSave(true);
            }
        }
    }

    private boolean postcodeIndexIsSubmitted(AssessorRegistrationForm assessorRegistrationForm) {
        return StringUtils.hasText(assessorRegistrationForm.getAddressForm().getSelectedPostcodeIndex());
    }

    private boolean postcodeIsSelected(AssessorRegistrationForm assessorRegistrationForm) {
        if (assessorRegistrationForm.getAddressForm() == null) {
            return false;
        }
        return assessorRegistrationForm.getAddressForm().getSelectedPostcode() != null;
    }

    private void addAddressOptions(AssessorRegistrationForm registrationForm) {
        if (StringUtils.hasText(registrationForm.getAddressForm().getPostcodeInput())) {
            AddressForm addressForm = registrationForm.getAddressForm();
            addressForm.setPostcodeOptions(searchPostcode(registrationForm.getAddressForm().getPostcodeInput()));
            addressForm.setPostcodeInput(registrationForm.getAddressForm().getPostcodeInput());
            registrationForm.setAddressForm(addressForm);
        }
    }

    private void addSelectedAddress(AssessorRegistrationForm registrationForm) {
        AddressForm addressForm = registrationForm.getAddressForm();
        if (postcodeIndexIsSubmitted(registrationForm)) {
            try {
                AddressResource selectedAddress = addressForm.getPostcodeOptions().get(
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

    private String doViewYourDetails(Model model, String inviteHash) {
        model.addAttribute("model", yourDetailsModelPopulator.populateModel(inviteHash));
        model.addAttribute("ethnicityOptions", getEthnicityOptions());
        return "registration/register";
    }

    private List<EthnicityResource> getEthnicityOptions() {
        return ethnicityRestService.findAllActive().getSuccessObjectOrThrowException();
    }
}
