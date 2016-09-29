package com.worth.ifs.assessment.controller;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.service.AddressRestService;
import com.worth.ifs.assessment.form.AssessorRegistrationDeclarationForm;
import com.worth.ifs.assessment.form.AssessorRegistrationForm;
import com.worth.ifs.assessment.form.AssessorRegistrationSkillsForm;
import com.worth.ifs.assessment.form.AssessorRegistrationTermsForm;
import com.worth.ifs.assessment.model.*;
import com.worth.ifs.assessment.service.AssessorService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.form.AddressForm;
import com.worth.ifs.invite.service.EthnicityRestService;
import com.worth.ifs.user.resource.EthnicityResource;
import com.worth.ifs.util.CookieUtil;
import com.worth.ifs.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * Controller to manage Assessor Registration.
 */
@Controller
@RequestMapping("/registration")
public class AssessorRegistrationController {

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
    private AssessorRegistrationYourDetailsModelPopulator yourDetailsModelPopulator;

    @Autowired
    private AssessorRegistrationSkillsModelPopulator assessorSkillsModelPopulator;

    @Autowired
    private AssessorRegistrationDeclarationModelPopulator assessorRegistrationDeclarationModelPopulator;

    @Autowired
    private AssessorRegistrationTermsModelPopulator assessorRegistrationTermsModelPopulator;

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
                                    @SuppressWarnings("unused") BindingResult bindingResult,
                                    ValidationHandler validationHandler) {

        addAddressOptions(registrationForm);
        addSelectedAddress(registrationForm);
        validateAddressForm(registrationForm, bindingResult);

        Supplier<String> failureView = () -> doViewYourDetails(model, inviteHash);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> result = assessorService.createAssessorByInviteHash(inviteHash, registrationForm);
            return validationHandler.addAnyErrors(result, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> "redirect:/registration/skills");
        });
    }

    @RequestMapping(value = "/{inviteHash}/register", params = "manual-address", method = RequestMethod.POST)
    public String manualAddress(Model model,
                                @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationForm registrationForm,
                                @PathVariable("inviteHash") String inviteHash,
                                HttpServletRequest request, HttpServletResponse response) {
        registrationForm.setAddressForm(new AddressForm());
        registrationForm.getAddressForm().setManualAddress(true);

        return doViewYourDetails(model, inviteHash);
    }

    @RequestMapping(value = "/{inviteHash}/register", params = "search-address", method = RequestMethod.POST)
    public String searchAddress(Model model,
                                @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationForm registrationForm,
                                @PathVariable("inviteHash") String inviteHash,
                                HttpServletRequest request, HttpServletResponse response) {
        addAddressOptions(registrationForm);

        return doViewYourDetails(model, inviteHash);
    }

    @RequestMapping(value = "/{inviteHash}/register", params = "select-address", method = RequestMethod.POST)
    public String selectAddress(Model model,
                                @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationForm registrationForm,
                                @PathVariable("inviteHash") String inviteHash,
                                HttpServletRequest request, HttpServletResponse response) {
        addAddressOptions(registrationForm);
        addSelectedAddress(registrationForm);

        return doViewYourDetails(model, inviteHash);
    }

    private void validateAddressForm(AssessorRegistrationForm assessorRegistrationForm, BindingResult addressBindingResult) {
        if(postcodeIsSelected(assessorRegistrationForm)) {
            validator.validate(assessorRegistrationForm.getAddressForm().getSelectedPostcode(), addressBindingResult);
        }
    }

    private boolean postcodeIsSelected(AssessorRegistrationForm assessorRegistrationForm) {
        return assessorRegistrationForm.getAddressForm().getSelectedPostcode() != null;
    }

    @RequestMapping(value = "skills", method = RequestMethod.GET)
    public String getSkills(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationSkillsForm form) {
        model.addAttribute("model", assessorSkillsModelPopulator.populateModel());
        return "registration/innovation-areas";
    }

    @RequestMapping(value = "skills", method = RequestMethod.POST)
    public String submitSkills(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationSkillsForm form) {
        return "redirect:/registration/declaration";
    }

    @RequestMapping(value = "declaration", method = RequestMethod.GET)
    public String getDeclaration(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationDeclarationForm form) {
        model.addAttribute("model", assessorRegistrationDeclarationModelPopulator.populateModel());
        return "registration/declaration-of-interest";
    }

    @RequestMapping(value = "declaration", method = RequestMethod.POST)
    public String submitDeclaration(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationDeclarationForm form) {
        return "redirect:/registration/terms";
    }

    @RequestMapping(value = "terms", method = RequestMethod.GET)
    public String getTerms(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationTermsForm form) {
        model.addAttribute("model", assessorRegistrationTermsModelPopulator.populateModel());
        return "registration/terms";
    }

    @RequestMapping(value = "terms", method = RequestMethod.POST)
    public String submitTerms(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationTermsForm form) {
        return "redirect:/assessor/dashboard";
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
        if (StringUtils.hasText(addressForm.getSelectedPostcodeIndex()) && addressForm.getSelectedPostcode() == null) {
            addressForm.setSelectedPostcode(addressForm.getPostcodeOptions().get(Integer.parseInt(addressForm.getSelectedPostcodeIndex())));
            registrationForm.setAddressForm(addressForm);
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