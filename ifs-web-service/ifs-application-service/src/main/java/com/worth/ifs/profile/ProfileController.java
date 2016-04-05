package com.worth.ifs.profile;

import com.worth.ifs.user.service.UserService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * This controller will handle all requests that are related to a user profile.
 */

@Controller
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private UserService userService;

    @Autowired
    UserAuthenticationService userAuthenticationService;


    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public String viewUserProfile(Model model, HttpServletRequest request) {
        String destination = "profile/user-profile";
        populateUserDetailsForm(model, request);
        boolean userIsLoggedIn = userIsLoggedIn(request);
        model.addAttribute("userIsLoggedIn", userIsLoggedIn);
        return destination;
    }

    private void populateUserDetailsForm(Model model, HttpServletRequest request){
        final UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        UserDetailsForm userDetailsForm = buildUserDetailsForm(user);
        setFormActionURL(userDetailsForm);
        model.addAttribute("userDetailsForm", userDetailsForm);
    }

    private UserDetailsForm buildUserDetailsForm(final UserResource user){
        UserDetailsForm form = new UserDetailsForm();
        form.setEmail(user.getEmail());
        form.setFirstName(user.getFirstName());
        form.setLastName(user.getLastName());
        form.setTitle(user.getTitle());
        form.setPhoneNumber(user.getPhoneNumber());
        return form;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String submitUserProfile(@Valid @ModelAttribute UserDetailsForm userDetailsForm, BindingResult bindingResult,
                                    Model model, HttpServletRequest request) {
        String destination = "profile/edit-user-profile";

        boolean userIsLoggedIn = userIsLoggedIn(request);
        model.addAttribute("userIsLoggedIn", userIsLoggedIn);
        final UserResource loggedInUser = userAuthenticationService.getAuthenticatedUser(request);

        if(!bindingResult.hasErrors()) {
            RestResult<UserResource> updateProfileResult = updateUser(loggedInUser, userDetailsForm);

            if (updateProfileResult.isSuccess()) {
                destination = viewUserProfile(model, request);
            } else {
                addEnvelopeErrorsToBindingResultErrors(updateProfileResult.getFailure().getErrors(), bindingResult);
            }
        }

        return destination;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String editUserProfile(HttpServletRequest request, Model model) {
        populateUserDetailsForm(model, request);
        boolean userIsLoggedIn = userIsLoggedIn(request);
        model.addAttribute("userIsLoggedIn", userIsLoggedIn);
        return "profile/edit-user-profile";
    }

    private boolean userIsLoggedIn(HttpServletRequest request) {
        Authentication authentication = userAuthenticationService.getAuthentication(request);

        return authentication != null;
    }

    private void setFormActionURL(UserDetailsForm userDetailsForm) {
        userDetailsForm.setActionUrl("/profile/edit");
    }

    private RestResult<UserResource> updateUser(final UserResource loggedInUser, UserDetailsForm userDetailsForm) {
        return userService.updateDetails(
                loggedInUser.getEmail(),
                userDetailsForm.getFirstName(),
                userDetailsForm.getLastName(),
                userDetailsForm.getTitle(),
                userDetailsForm.getPhoneNumber());
    }

    private void addEnvelopeErrorsToBindingResultErrors(List<Error> errors, BindingResult bindingResult) {
        errors.forEach(
                error -> bindingResult.addError(
                        new ObjectError(
                                error.getErrorKey(),
                                error.getErrorMessage()
                        )
                )
        );
    }
}
