package com.worth.ifs.assessment.controller;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.service.AddressRestService;
import com.worth.ifs.assessment.form.AssessorRegistrationForm;
import com.worth.ifs.assessment.model.AssessorRegistrationBecomeAnAssessorModelPopulator;
import com.worth.ifs.assessment.model.AssessorRegistrationModelPopulator;
import com.worth.ifs.assessment.service.AssessorRestService;
import com.worth.ifs.assessment.service.CompetitionInviteRestService;
import com.worth.ifs.assessment.viewmodel.AssessorRegistrationViewModel;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.form.AddressForm;
import com.worth.ifs.invite.service.EthnicityRestService;
import com.worth.ifs.user.resource.EthnicityResource;
import com.worth.ifs.user.resource.UserResource;
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
    private CompetitionInviteRestService inviteRestService;

    @Autowired
    private EthnicityRestService ethnicityRestService;

    @Autowired
    private AssessorRegistrationBecomeAnAssessorModelPopulator becomeAnAssessorModelPopulator;

    @Autowired
    private AssessorRegistrationModelPopulator registrationModelPopulator;

    @Autowired
    private AssessorRestService assessorRestService;

    @RequestMapping(value = "/{inviteHash}/start", method = RequestMethod.GET)
    public String becomeAnAssessor(Model model, @PathVariable("inviteHash") String inviteHash) {

        model.addAttribute("model", becomeAnAssessorModelPopulator.populateModel(inviteHash));
        return "registration/become-assessor";
    }

    @RequestMapping(value = "/{inviteHash}/register", method = RequestMethod.GET)
    public String registerForm(Model model,
                               @ModelAttribute("registrationForm") AssessorRegistrationForm form,
                               BindingResult bindingResult,
                               @PathVariable("inviteHash") String inviteHash,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        addRegistrationFormToModel(model, inviteHash);
        return "registration/register";
    }

    private void addRegistrationFormToModel(Model model, String inviteHash) {
        AssessorRegistrationForm registrationForm = new AssessorRegistrationForm();
        model.addAttribute("registrationForm", registrationForm);
        model.addAttribute("ethnicityOptions", getEthnicityOptions());
        model.addAttribute("model", registrationModelPopulator.populateModel(inviteHash));
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerFormSubmit(@Valid @ModelAttribute("registrationForm") AssessorRegistrationForm registrationForm,
                                     BindingResult bindingResult,
                                     HttpServletResponse response,
                                     HttpServletRequest request,
                                     Model model) {

        addAddressOptions(registrationForm);

        return "registration/register";
    }


    private RestResult<UserResource> createUser(String hash, AssessorRegistrationForm registrationForm, Model model) {
        AssessorRegistrationViewModel viewModel = (AssessorRegistrationViewModel) model.asMap().get("model");

        return assessorRestService.createAssessorByInviteHash(
                viewModel.getCompetitionInviteHash(),
                registrationForm.getFirstName(),
                registrationForm.getLastName(),
                registrationForm.getPassword(),
                viewModel.getEmail(),
                registrationForm.getTitle(),
                registrationForm.getPhoneNumber(),
                registrationForm.getGender(),
                registrationForm.getDisability(),
                registrationForm.getEthnicity().getId()
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

    private List<EthnicityResource> getEthnicityOptions() {
        return ethnicityRestService.findAllActive().getSuccessObjectOrThrowException();
    }

}