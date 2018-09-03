package org.innovateuk.ifs.eugrant.organisation.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.eugrant.organisation.form.EuOrganisationTypeForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Provides methods for picking an organisation type as a lead applicant after initialization or the registration process.
 */

@Controller
@RequestMapping(AbstractEuOrganisationController.BASE_URL + "/" + AbstractEuOrganisationController.ORGANISATION_TYPE)
@SecuredBySpring(value = "Controller", description = "TODO", securedType = EuOrganisationTypeController.class)
@PreAuthorize("permitAll")
public class EuOrganisationTypeController extends AbstractEuOrganisationController {

    @GetMapping
    public String selectOrganisationType(Model model,
                                         HttpServletRequest request) {
        model.addAttribute(ORGANISATION_FORM, organisationCookieService.getOrganisationTypeCookieValue()
                .orElse(new EuOrganisationTypeForm()));
        return TEMPLATE_PATH + "/" + ORGANISATION_TYPE;
    }

    @PostMapping
    public String confirmSelectOrganisationType(@Valid @ModelAttribute(ORGANISATION_FORM) EuOrganisationTypeForm typeForm,
                                                BindingResult bindingResult,
                                                ValidationHandler validationHandler) {
        return validationHandler.failNowOrSucceedWith(() -> TEMPLATE_PATH + "/" + ORGANISATION_TYPE,
                () -> {
                    organisationCookieService.saveToOrganisationTypeCookie(typeForm);
                    return "redirect:" + BASE_URL + "/" + FIND_ORGANISATION;
                }
        );
    }
}