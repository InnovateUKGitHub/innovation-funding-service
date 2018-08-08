package org.innovateuk.ifs.profile.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.user.resource.Disability;
import org.innovateuk.ifs.user.resource.Gender;
import org.innovateuk.ifs.user.resource.Title;
import org.innovateuk.ifs.user.service.OrganisationService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.service.EthnicityRestService;
import org.innovateuk.ifs.profile.form.UserDetailsForm;
import org.innovateuk.ifs.profile.populator.UserProfilePopulator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * This controller will handle all requests that are related to a user profile.
 */

@Controller
@RequestMapping("/profile")
@SecuredBySpring(value="Controller", description = "TODO", securedType = ProfileController.class)
@PreAuthorize("hasAuthority('applicant')")
public class ProfileController {
    private static final Log LOG = LogFactory.getLog(ProfileController.class);

    @Autowired
    private UserService userService;
    
    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private EthnicityRestService ethnicityRestService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private UserProfilePopulator userProfilePopulator;

    @GetMapping("/view")
    public String viewUserProfile(Model model,
                                  UserResource userResource) {

        model.addAttribute("model", userProfilePopulator.populate(userResource));
        return "profile/user-profile";
    }

    private void populateUserDetailsForm(Model model, UserResource userResource){
        UserDetailsForm userDetailsForm = buildUserDetailsForm(userResource);
        setFormActionURL(userDetailsForm);
        model.addAttribute("userDetailsForm", userDetailsForm);
    }
    
	private UserDetailsForm buildUserDetailsForm(final UserResource user){
        UserDetailsForm form = new UserDetailsForm();
        form.setEmail(user.getEmail());
        form.setFirstName(user.getFirstName());
        form.setLastName(user.getLastName());
        form.setPhoneNumber(user.getPhoneNumber());
        form.setAllowMarketingEmails(user.getAllowMarketingEmails());
		return form;
    }

    @PostMapping("/edit")
    public String submitUserProfile(@Valid @ModelAttribute("userDetailsForm") UserDetailsForm userDetailsForm, BindingResult bindingResult,
                                    Model model,
                                    UserResource loggedInUser,
                                    HttpServletRequest request) {
        String destination = "profile/edit-user-profile";

        if(!bindingResult.hasErrors()) {
            ServiceResult<UserResource> updateProfileResult = updateUser(loggedInUser, userDetailsForm);

            if (updateProfileResult.isSuccess()) {
                loggedInUser = userAuthenticationService.getAuthenticatedUser(request, true);
                destination = viewUserProfile(model, loggedInUser);
            } else {
                addEnvelopeErrorsToBindingResultErrors(updateProfileResult.getFailure().getErrors(), bindingResult);
            }
        }

        return destination;
    }

    @GetMapping("/edit")
    public String editUserProfile(UserResource user,
                                  HttpServletRequest request, Model model) {
        populateUserDetailsForm(model, user);
        return "profile/edit-user-profile";
    }

    private void setFormActionURL(UserDetailsForm userDetailsForm) {
        userDetailsForm.setActionUrl("/profile/edit");
    }

    private ServiceResult<UserResource> updateUser(final UserResource loggedInUser, UserDetailsForm userDetailsForm) {
        return userService.updateDetails(
                loggedInUser.getId(),
                loggedInUser.getEmail(),
                userDetailsForm.getFirstName(),
                userDetailsForm.getLastName(),
                ofNullable(loggedInUser.getTitle()).map(Title::getDisplayName).orElse(null),
                userDetailsForm.getPhoneNumber(),
                ofNullable(loggedInUser.getGender()).map(Gender::getDisplayName).orElse(null),
                loggedInUser.getEthnicity(),
                ofNullable(loggedInUser.getDisability()).map(Disability::getDisplayName).orElse(null),
                userDetailsForm.getAllowMarketingEmails());
    }

    private void addEnvelopeErrorsToBindingResultErrors(List<Error> errors, BindingResult bindingResult) {
        errors.forEach(
            error -> bindingResult.addError(new ObjectError(error.getErrorKey(), new String[] {error.getErrorKey()}, null, null))
        );
    }
}
