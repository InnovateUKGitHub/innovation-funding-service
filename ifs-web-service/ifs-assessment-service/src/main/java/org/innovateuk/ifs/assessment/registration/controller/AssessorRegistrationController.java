package org.innovateuk.ifs.assessment.registration.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.service.AddressRestService;
import org.innovateuk.ifs.assessment.registration.form.AssessorRegistrationForm;
import org.innovateuk.ifs.assessment.registration.populator.AssessorRegistrationBecomeAnAssessorModelPopulator;
import org.innovateuk.ifs.assessment.registration.populator.AssessorRegistrationModelPopulator;
import org.innovateuk.ifs.assessment.registration.service.AssessorService;
import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.address.form.AddressForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.address.form.AddressForm.FORM_ACTION_PARAMETER;

/**
 * Controller to manage Assessor Registration.
 */
@Controller
@RequestMapping("/registration")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = AssessorRegistrationController.class)
@PreAuthorize("permitAll")
public class AssessorRegistrationController {
    private static final Log LOG = LogFactory.getLog(AssessorRegistrationController.class);

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private AddressRestService addressRestService;

    @Autowired
    private AssessorService assessorService;

    @Autowired
    private AssessorRegistrationBecomeAnAssessorModelPopulator becomeAnAssessorModelPopulator;

    @Autowired
    private AssessorRegistrationModelPopulator yourDetailsModelPopulator;

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

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
                              @ModelAttribute(name = FORM_ATTR_NAME, binding = false) AssessorRegistrationForm form) {
        return doViewYourDetails(model, inviteHash);
    }

    @PostMapping("/{inviteHash}/register")
    public String submitYourDetails(Model model,
                                    @PathVariable("inviteHash") String inviteHash,
                                    @Valid @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationForm registrationForm,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> doViewYourDetails(model, inviteHash);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ServiceResult<Void> result = assessorService.createAssessorByInviteHash(inviteHash, registrationForm,
                    registrationForm.getAddressForm().getSelectedAddress(this::searchPostcode));

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
    public String accountCreated(Model model, @PathVariable("inviteHash") String inviteHash, UserResource loggedInUser) {
        boolean userIsLoggedIn = loggedInUser != null;

        // the user is already logged in, take them back to the invite
        if (userIsLoggedIn) {
            return format("redirect:/invite/competition/%s", inviteHash);
        }

        return competitionInviteRestService.checkExistingUser(inviteHash).andOnSuccessReturn(userExists -> {
            if (!userExists) {
                // reached here without creating an assessor, redirect back to the invite
                return format("redirect:/invite/competition/%s", inviteHash);
            }
            else {
                model.addAttribute("competitionInviteHash", inviteHash);
                return "registration/account-created";
            }
        }).getSuccess();
    }

    @PostMapping(value = "/{inviteHash}/register", params = FORM_ACTION_PARAMETER)
    public String addressFormAction(Model model,
                                @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationForm registrationForm,
                                BindingResult bindingResult,
                                ValidationHandler validationHandler,
                                @PathVariable("inviteHash") String inviteHash) {

        registrationForm.getAddressForm().validateAction(bindingResult);
        if (validationHandler.hasErrors()) {
            return doViewYourDetails(model, inviteHash);
        }

        AddressForm addressForm = registrationForm.getAddressForm();
        addressForm.handleAction(this::searchPostcode);

        return doViewYourDetails(model, inviteHash);
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
        return "registration/register";
    }
}
