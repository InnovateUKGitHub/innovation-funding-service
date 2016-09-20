package com.worth.ifs.assessment.controller;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.service.AddressRestService;
import com.worth.ifs.assessment.form.AssessorRegistrationForm;
import com.worth.ifs.assessment.model.AssessorRegistrationBecomeAnAssessorModelPopulator;
import com.worth.ifs.assessment.model.AssessorRegistrationModelPopulator;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.form.AddressForm;
import com.worth.ifs.util.CookieUtil;
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
                               HttpServletRequest request,
                               HttpServletResponse response) {

        addRegistrationFormToModel(model, request, response);

        model.addAttribute("model", registrationModelPopulator.populateModel(inviteHash));

        String destination = "registration/register";

        return destination;
    }

    private void addRegistrationFormToModel(Model model, HttpServletRequest request, HttpServletResponse response) {
        AssessorRegistrationForm registrationForm = new AssessorRegistrationForm();
        model.addAttribute("registrationForm", registrationForm);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerFormSubmit(@Valid @ModelAttribute("registrationForm") AssessorRegistrationForm registrationForm,
                                     BindingResult bindingResult,
                                     HttpServletResponse response,
                                     HttpServletRequest request,
                                     Model model) {
        addAddressOptions(registrationForm);

        //Retrieve invite
        //Add email address to model
        //Attempt account creation
        //Login user automatically
        //Redirect user to details page

        String destination = "registration/register";

        return destination;
    }

    private RestResult<UserResource> createUser(String hash, RegistrationForm registrationForm) {
        return assessorRestService.createAssessorByInviteHash(
                hash,
                registrationForm.getFirstName(),
                registrationForm.getLastName(),
                registrationForm.getPassword(),
                registrationForm.getEmail(),
                registrationForm.getTitle(),
                registrationForm.getPhoneNumber(),
                "",
                "",
                1L
                );
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