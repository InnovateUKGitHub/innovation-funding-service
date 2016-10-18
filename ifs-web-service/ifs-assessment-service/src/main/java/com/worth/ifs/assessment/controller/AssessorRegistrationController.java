package com.worth.ifs.assessment.controller;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.service.AddressRestService;
import com.worth.ifs.assessment.form.registration.AssessorRegistrationForm;
import com.worth.ifs.assessment.model.registration.AssessorRegistrationBecomeAnAssessorModelPopulator;
import com.worth.ifs.assessment.model.registration.AssessorRegistrationModelPopulator;
import com.worth.ifs.assessment.service.AssessorService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.form.AddressForm;
import com.worth.ifs.invite.service.EthnicityRestService;
import com.worth.ifs.user.resource.EthnicityResource;
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
import static java.lang.String.format;

/**
 * Controller to manage Assessor Registration.
 */
@Controller
@RequestMapping("/registration")
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

    private Validator validator;

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    @RequestMapping(value = "/{inviteHash}/start", method = RequestMethod.GET)
    public String becomeAnAssessor(Model model,
                                   @PathVariable("inviteHash") String inviteHash) {
        model.addAttribute("model", becomeAnAssessorModelPopulator.populateModel(inviteHash));
        return "registration/become-assessor";
    }

    @RequestMapping(value = "/{inviteHash}/register", method = RequestMethod.GET)
    public String yourDetails(Model model,
                              @PathVariable("inviteHash") String inviteHash,
                              @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationForm form) {
        return doViewYourDetails(model, inviteHash);
    }

    @RequestMapping(value = "/{inviteHash}/register", method = RequestMethod.POST)
    public String submitYourDetails(Model model,
                                    @PathVariable("inviteHash") String inviteHash,
                                    @Valid @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationForm registrationForm,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler) {

        addAddressOptions(registrationForm);
        addSelectedAddress(registrationForm);
        validateAddressForm(registrationForm, bindingResult);

        Supplier<String> failureView = () -> doViewYourDetails(model, inviteHash);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> result = assessorService.createAssessorByInviteHash(inviteHash, registrationForm);
            return validationHandler.addAnyErrors(result, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> format("redirect:/invite-accept/competition/%s/accept", inviteHash));
        });
    }

    @RequestMapping(value = "/{inviteHash}/register", params = "manual-address", method = RequestMethod.POST)
    public String manualAddress(Model model,
                                @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationForm registrationForm,
                                @PathVariable("inviteHash") String inviteHash) {
        registrationForm.setAddressForm(new AddressForm());
        registrationForm.getAddressForm().setManualAddress(true);

        return doViewYourDetails(model, inviteHash);
    }

    @RequestMapping(value = "/{inviteHash}/register", params = "search-address", method = RequestMethod.POST)
    public String searchAddress(Model model,
                                @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationForm registrationForm,
                                @PathVariable("inviteHash") String inviteHash,
                                BindingResult bindingResult, ValidationHandler validationHandler) {

        Supplier<String> view = () -> doViewYourDetails(model, inviteHash);

        addAddressOptions(registrationForm);
        registrationForm.getAddressForm().setTriedToSearch(true);

        if (registrationForm.getAddressForm().getPostcodeInput().isEmpty()) {
            bindingResult.rejectValue("addressForm.postcodeInput", "validation.standard.postcodesearch.required");
        }

        return validationHandler.failNowOrSucceedWith(view, view);
    }

    @RequestMapping(value = "/{inviteHash}/register", params = "select-address", method = RequestMethod.POST)
    public String selectAddress(Model model,
                                @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationForm registrationForm,
                                @PathVariable("inviteHash") String inviteHash) {
        addAddressOptions(registrationForm);
        addSelectedAddress(registrationForm);

        return doViewYourDetails(model, inviteHash);
    }

    private void validateAddressForm(AssessorRegistrationForm assessorRegistrationForm, BindingResult bindingResult) {
        if (postcodeIsSelected(assessorRegistrationForm)) {
            validator.validate(assessorRegistrationForm.getAddressForm().getSelectedPostcode(), bindingResult);
        } else {
            bindingResult.rejectValue("addressForm.postcodeInput", "validation.standard.postcodesearch.required");
        }
        assessorRegistrationForm.getAddressForm().setTriedToSearch(true);
        assessorRegistrationForm.getAddressForm().setTriedToSave(true);
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

    private String doViewYourDetails(Model model, String inviteHash) {
        model.addAttribute("model", yourDetailsModelPopulator.populateModel(inviteHash));
        model.addAttribute("ethnicityOptions", getEthnicityOptions());
        return "registration/register";
    }

    private List<EthnicityResource> getEthnicityOptions() {
        return ethnicityRestService.findAllActive().getSuccessObjectOrThrowException();
    }
}