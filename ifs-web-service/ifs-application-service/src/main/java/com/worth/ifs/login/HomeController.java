package com.worth.ifs.login;

import com.google.common.collect.Sets;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.user.resource.RoleResource;
import com.worth.ifs.user.resource.UserResource;

import com.worth.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static com.worth.ifs.user.resource.UserRoleType.APPLICANT;
import static com.worth.ifs.user.resource.UserRoleType.ASSESSOR;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.hasText;

/**
 * This Controller redirects the request from http://<domain>/ to http://<domain>/login
 * So we don't have a public homepage, the login page is the homepage.
 */
@Controller
public class HomeController {

    @Autowired
    UserAuthenticationService userAuthenticationService;

    public static String getRedirectUrlForUser(UserResource user) {

        String roleUrl = "";

        if (!user.getRoles().isEmpty()) {
            roleUrl = user.getRoles().get(0).getUrl();
        }

        return format("redirect:/%s", hasText(roleUrl) ? roleUrl : "dashboard");
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String login() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (unauthenticated(authentication)) {
            return "redirect:/";
        }

        UserResource user = (UserResource) authentication.getDetails();
        if (isAssessorAndApplicant(user)) {
            return doViewRoleSelection();
        }

        return getRedirectUrlForUser(user);
    }

    @RequestMapping(value = "/role", method = RequestMethod.GET)
    public String selectRole() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserResource user = (UserResource) authentication.getDetails();
        if (unauthenticated(authentication) || user.getRoles().size() == 1) {
            return "redirect:/";
        }

        return "/roleSelection";
    }

    @RequestMapping(value = "/role", method = RequestMethod.POST)
    public String processRole(Model model,
                              @ModelAttribute("loggedInUser") UserResource user,
                              @Valid @ModelAttribute("form") RoleSelectionForm form,
                              BindingResult bindingResult,
                              ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> doViewRoleSelection();

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ValidationMessages validationMessages = new ValidationMessages(bindingResult);
            return validationHandler.addAnyErrors(validationMessages, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> redirectToChosenDashboard(user, form.getSelectedRole().getName()));
        });
    }

    private String doViewRoleSelection() {
        return "/role";
    }

    private String redirectToChosenDashboard(UserResource user, String role) {
        List<RoleResource> roles = user.getRoles();
        String url = roles.stream().filter(roleResource -> roleResource.getName().equals(role)).findFirst().get().getUrl();

        return format("redirect:/%s", url);
    }

    private static boolean unauthenticated(Authentication authentication) {
        return authentication == null || !authentication.isAuthenticated() || authentication.getDetails() == null;
    }

    private boolean isAssessorAndApplicant(UserResource user) {
        Set<UserRoleType> acceptedRoles = Sets.newHashSet(APPLICANT, ASSESSOR);
        List<UserRoleType> userRoles = user.getRoles().stream().map(role -> UserRoleType.fromName(role.getName())).collect(toList());
        return userRoles.containsAll(acceptedRoles);
    }
}
