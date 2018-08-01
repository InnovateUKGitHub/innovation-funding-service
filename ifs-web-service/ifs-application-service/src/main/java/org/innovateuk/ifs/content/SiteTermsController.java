package org.innovateuk.ifs.content;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.TermsAndConditionsRestService;
import org.innovateuk.ifs.content.form.NewSiteTermsAndConditionsForm;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.security.StatelessAuthenticationFilter.SAVED_REQUEST_URL_COOKIE_NAME;

/**
 * This controller will handle all requests that are related to the Site Terms and Conditions.
 */
@Controller
@RequestMapping("/info")
@SecuredBySpring(value = "Controller", description = "Applicants can view and agree to Site Terms and Conditions",
        securedType = SiteTermsController.class)
@PreAuthorize("hasAuthority('applicant')")
public class SiteTermsController {

    @Autowired
    private TermsAndConditionsRestService termsAndConditionsRestService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private CookieUtil cookieUtil;

    @PreAuthorize("permitAll")
    @GetMapping("terms-and-conditions")
    public String termsAndConditions() {
        SiteTermsAndConditionsResource siteTermsAndConditions = termsAndConditionsRestService
                .getLatestSiteTermsAndConditions().getSuccess();
        return format("content/%s", siteTermsAndConditions.getTemplate());
    }

    @GetMapping("new-terms-and-conditions")
    public String newTermsAndConditions(@ModelAttribute(name = "form") NewSiteTermsAndConditionsForm form) {
        return "content/new-terms-and-conditions";
    }

    @PostMapping("new-terms-and-conditions")
    public String agreeNewTermsAndConditions(HttpServletRequest request,
                                             HttpServletResponse response,
                                             UserResource loggedInUser,
                                             @Valid @ModelAttribute(name = "form") NewSiteTermsAndConditionsForm form,
                                             @SuppressWarnings("unused") BindingResult bindingResult,
                                             ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> newTermsAndConditions(form);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            RestResult<Void> updateResult = userRestService.agreeNewSiteTermsAndConditions(loggedInUser.getId());

            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, redirectHandler(request, response));
        });
    }

    private Supplier<String> redirectHandler(HttpServletRequest request, HttpServletResponse response) {
        return () -> {
            String redirectUrl = getRedirectUrl(request);
            deleteSavedRequestUrlCookie(response);
            return redirectUrl;
        };
    }

    private String getRedirectUrl(HttpServletRequest request) {
        String redirectUrl = cookieUtil.getCookieValue(request, SAVED_REQUEST_URL_COOKIE_NAME);
        return format("redirect:%s", isNotBlank(redirectUrl) ? redirectUrl : "/");
    }

    private void deleteSavedRequestUrlCookie(HttpServletResponse response) {
        cookieUtil.removeCookie(response, SAVED_REQUEST_URL_COOKIE_NAME);
    }

}
