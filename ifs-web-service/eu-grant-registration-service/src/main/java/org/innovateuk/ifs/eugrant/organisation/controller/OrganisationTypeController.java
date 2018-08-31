package org.innovateuk.ifs.eugrant.organisation.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.eugrant.EuOrganisationType;
import org.innovateuk.ifs.eugrant.organisation.form.OrganisationTypeForm;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Optional;

/**
 * Provides methods for picking an organisation type as a lead applicant after initialization or the registration process.
 */

@Controller
@RequestMapping(AbstractOrganisationController.BASE_URL + "/" + AbstractOrganisationController.ORGANISATION_TYPE)
@SecuredBySpring(value = "Controller", description = "TODO", securedType = OrganisationTypeController.class)
@PreAuthorize("permitAll")
public class OrganisationTypeController extends AbstractOrganisationController {

    @Autowired
    private CompetitionRestService competitionRestService;

    @GetMapping
    public String selectOrganisationType(Model model,
                                         HttpServletRequest request) {
        Optional<OrganisationTypeForm> organisationTypeCookieValue = organisationCookieService.getOrganisationTypeCookieValue();
        if (organisationTypeCookieValue.isPresent()) {
            model.addAttribute(ORGANISATION_FORM, organisationTypeCookieValue.get());
        } else {
            model.addAttribute(ORGANISATION_FORM, new OrganisationTypeForm());
        }
        return TEMPLATE_PATH + "/" + ORGANISATION_TYPE;
    }

    @PostMapping
    public String confirmSelectOrganisationType(@Valid @ModelAttribute(ORGANISATION_FORM) OrganisationTypeForm typeForm,
                                                BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            EuOrganisationType organisationType = typeForm.getOrganisationType();
            OrganisationTypeForm organisationTypeForm = organisationCookieService.getOrganisationTypeCookieValue().orElse(new OrganisationTypeForm());
            organisationTypeForm.setOrganisationType(organisationType);
            organisationCookieService.saveToOrganisationTypeCookie(organisationTypeForm);
            return "redirect:" + BASE_URL + "/" + FIND_ORGANISATION;
        } else {
            return TEMPLATE_PATH + "/" + ORGANISATION_TYPE;
        }
    }
}