package com.worth.ifs.assessment.controller;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.service.AddressRestService;
import com.worth.ifs.assessment.form.AssessorRegistrationForm;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.exception.InviteAlreadyAcceptedException;
import com.worth.ifs.form.AddressForm;
import com.worth.ifs.registration.form.RegistrationForm;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerForm(Model model, HttpServletRequest request, HttpServletResponse response) {

        try {
            addRegistrationFormToModel(model, request, response);
        }
        catch (InviteAlreadyAcceptedException e) {
            return "redirect:/login";
        }

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
        String destination = "register";

        return destination;
    }

    private void addAddressOptions(AssessorRegistrationForm organisationForm) {
        if (StringUtils.hasText(organisationForm.getAddressForm().getPostcodeInput())) {
            AddressForm addressForm = organisationForm.getAddressForm();
            addressForm.setPostcodeOptions(searchPostcode(organisationForm.getAddressForm().getPostcodeInput()));
            addressForm.setPostcodeInput(organisationForm.getAddressForm().getPostcodeInput());
            organisationForm.setAddressForm(addressForm);
        }
    }

    private List<AddressResource> searchPostcode(String postcodeInput) {
        RestResult<List<AddressResource>>  addressLookupRestResult =
                addressRestService.doLookup(postcodeInput);
        List<AddressResource> addressResourceList = addressLookupRestResult.handleSuccessOrFailure(
                failure -> new ArrayList<>(),
                addresses -> addresses);
        return addressResourceList;
    }
}