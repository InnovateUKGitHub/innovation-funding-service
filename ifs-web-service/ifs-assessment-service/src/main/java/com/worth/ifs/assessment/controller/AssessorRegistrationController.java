package com.worth.ifs.assessment.controller;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.service.AddressRestService;
import com.worth.ifs.assessment.form.AssessorRegistrationForm;
import com.worth.ifs.assessment.model.AssessorRegistrationBecomeAnAssessorModelPopulator;
import com.worth.ifs.assessment.model.AssessorRegistrationModelPopulator;
import com.worth.ifs.assessment.service.AssessorRestService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.form.AddressForm;
import com.worth.ifs.registration.resource.UserRegistrationResource;
import com.worth.ifs.user.resource.Disability;
import com.worth.ifs.user.resource.EthnicityResource;
import com.worth.ifs.user.resource.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller to manage Assessor Registration.
 */
@Controller
@RequestMapping("/registration")
public class AssessorRegistrationController {

    @Autowired
    private AddressRestService addressRestService;

    @Autowired
    private AssessorRestService assessorRestService;

    @Autowired
    private AssessorRegistrationBecomeAnAssessorModelPopulator becomeAnAssessorModelPopulator;

    @Autowired
    private AssessorRegistrationModelPopulator registrationModelPopulator;

    @RequestMapping(value = "/{inviteHash}/start", method = RequestMethod.GET)
    public String becomeAnAssessor(Model model,
                                   @PathVariable("inviteHash") String inviteHash) {
        model.addAttribute("model", becomeAnAssessorModelPopulator.populateModel(inviteHash));
        return "registration/become-assessor";
    }

    @RequestMapping(value = "/{inviteHash}/register", method = RequestMethod.GET)
    public String registerForm(Model model,
                               @PathVariable("inviteHash") String inviteHash,
                               @ModelAttribute("form") AssessorRegistrationForm form,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        model.addAttribute("model", registrationModelPopulator.populateModel(inviteHash));
        return "registration/register";
    }

    @RequestMapping(value = "/{inviteHash}/register", method = RequestMethod.POST)
    public String registerFormSubmit(Model model,
                                     @PathVariable("inviteHash") String inviteHash,
                                     @Valid @ModelAttribute("registrationForm") AssessorRegistrationForm registrationForm,
                                     BindingResult bindingResult,
                                     ValidationHandler validationHandler) {

        addAddressOptions(registrationForm);

        UserRegistrationResource userRegistrationResource = new UserRegistrationResource();
        userRegistrationResource.setTitle(registrationForm.getTitle());
        userRegistrationResource.setFirstName(registrationForm.getFirstName());
        userRegistrationResource.setLastName(registrationForm.getLastName());
        userRegistrationResource.setPhoneNumber(registrationForm.getPhoneNumber());
        //TODO: Properly attach Gender/Disability/Ethnicity from registrationForm
        userRegistrationResource.setGender(Gender.NOT_STATED);
        userRegistrationResource.setDisability(Disability.NOT_STATED);
        userRegistrationResource.setEthnicity(new EthnicityResource());

        assessorRestService.createAssessorByInviteHash(inviteHash, userRegistrationResource).getSuccessObjectOrThrowException();

        return "registration/register";
    }

    private void addAddressOptions(AssessorRegistrationForm registrationForm) {
        if (StringUtils.hasText(registrationForm.getAddressForm().getPostcodeInput())) {
            AddressForm addressForm = registrationForm.getAddressForm();
            addressForm.setPostcodeOptions(searchPostcode(registrationForm.getAddressForm().getPostcodeInput()));
            addressForm.setPostcodeInput(registrationForm.getAddressForm().getPostcodeInput());
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
}