package org.innovateuk.ifs.content;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.TermsAndConditionsRestService;
import org.innovateuk.ifs.content.form.NewSiteTermsAndConditionsForm;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * This controller will handle all requests that are related to the Site Terms and Conditions.
 */
@Controller
@RequestMapping("/info")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = SiteTermsController.class)
@PreAuthorize("permitAll")
public class SiteTermsController {

    @Autowired
    private TermsAndConditionsRestService termsAndConditionsRestService;

    @Autowired
    private UserService userService;

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
    public String agreeNewTermsAndConditions(UserResource loggedInUser,
                                             @Valid @ModelAttribute(name = "form") NewSiteTermsAndConditionsForm form,
                                             @SuppressWarnings("unused") BindingResult bindingResult,
                                             ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> newTermsAndConditions(form);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ServiceResult<Void> updateResult = userService.agreeNewTermsAndConditions(loggedInUser.getId());

            // TODO IFS-3093 Where should the user be redirected to?
            String redirect = "redirect:/applicant/dashboard";

            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () -> redirect);
        });
    }
}
