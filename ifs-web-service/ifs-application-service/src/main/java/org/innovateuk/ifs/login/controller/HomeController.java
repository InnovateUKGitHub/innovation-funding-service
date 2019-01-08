package org.innovateuk.ifs.login.controller;

import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.login.form.RoleSelectionForm;
import org.innovateuk.ifs.login.viewmodel.RoleSelectionViewModel;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirstMandatory;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.springframework.util.StringUtils.hasText;

/**
 * This Controller redirects the request from http://<domain>/ to http://<domain>/login
 * So we don't have a public homepage, the login page is the homepage.
 */
@Controller
@SecuredBySpring(value = "Controller", description = "TODO", securedType = HomeController.class)
@PreAuthorize("permitAll")
public class HomeController {

    @Autowired
    private CookieUtil cookieUtil;

    @Value("${ifs.acc.landing.page.url}")
    private String accLandingPageUrl;

    private static final Map<Role, String> DEFAULT_LANDING_PAGE_URLS_FOR_ROLES =
            asMap(
                    ASSESSOR, "assessment/assessor/dashboard",
                    APPLICANT, "applicant/dashboard",
                    COMP_ADMIN, "management/dashboard",
                    PROJECT_FINANCE, "management/dashboard",
                    INNOVATION_LEAD, "management/dashboard",
                    IFS_ADMINISTRATOR, "management/dashboard",
                    SUPPORT, "management/dashboard",
                    MONITORING_OFFICER, "applicant/dashboard",
                    STAKEHOLDER, "management/dashboard"
            );

    @GetMapping("/")
    public String login() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (unauthenticated(authentication)) {
            return "redirect:/";
        }

        UserResource user = (UserResource) authentication.getDetails();

        if (user.hasMoreThanOneRoleOf(ASSESSOR, APPLICANT, STAKEHOLDER, ACC_USER)) {
            return "redirect:/roleSelection";
        }

        return getRedirectUrlForUser(user);
    }

    @GetMapping("/roleSelection")
    public String selectRole(Model model,
                             @ModelAttribute(name = "form", binding = false) RoleSelectionForm form) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserResource user = (UserResource) authentication.getDetails();
        if (unauthenticated(authentication) || (!user.hasMoreThanOneRoleOf(ASSESSOR, APPLICANT, STAKEHOLDER, ACC_USER))){
            return "redirect:/";
        }

        return doViewRoleSelection(model, user);
    }

    @PostMapping("/roleSelection")
    public String processRole(Model model,
                              UserResource user,
                              @Valid @ModelAttribute("form") RoleSelectionForm form,
                              BindingResult bindingResult,
                              ValidationHandler validationHandler,
                              HttpServletResponse response) {

        Supplier<String> failureView = () -> doViewRoleSelection(model, user);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ValidationMessages validationMessages = new ValidationMessages(bindingResult);
            cookieUtil.saveToCookie(response, "role", form.getSelectedRole().getName());
            return validationHandler.addAnyErrors(validationMessages, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> redirectToChosenDashboard(user, form.getSelectedRole().getName()));
        });
    }

    private String doViewRoleSelection(Model model, UserResource user) {
        model.addAttribute("model", new RoleSelectionViewModel(user));
        return "login/multiple-user-choice";
    }

    private String redirectToChosenDashboard(UserResource user, String roleName) {
        Role chosenRole = simpleFindFirstMandatory(user.getRoles(), role -> role.getName().equals(roleName));
        return getLandingPageForRole(chosenRole);
    }

    private static boolean unauthenticated(Authentication authentication) {
        return authentication == null || !authentication.isAuthenticated() || authentication.getDetails() == null;
    }

    private String getRedirectUrlForUser(UserResource user) {

        if (user.getRoles().isEmpty()) {
            return "";
        }

        Role role = user.getRoles().get(0);
        return getLandingPageForRole(role);
    }

    private String getLandingPageForRole(Role role) {

        if (ACC_USER.equals(role)) {
            return "redirect:" + accLandingPageUrl;
        }

        String roleUrl = DEFAULT_LANDING_PAGE_URLS_FOR_ROLES.get(role);

        return format("redirect:/%s", hasText(roleUrl) ? roleUrl : "dashboard");
    }
}
