package org.innovateuk.ifs.eugrant.organisation.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.eugrant.EuOrganisationType;
import org.innovateuk.ifs.eugrant.organisation.form.OrganisationCreationForm;
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
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

/**
 * Provides methods for picking an organisation type as a lead applicant after initialization or the registration process.
 */

@Controller
@RequestMapping(AbstractOrganisationCreationController.BASE_URL + "/" + AbstractOrganisationCreationController.ORGANISATION_TYPE)
@SecuredBySpring(value = "Controller", description = "TODO", securedType = OrganisationCreationLeadTypeController.class)
@PreAuthorize("permitAll")
public class OrganisationCreationLeadTypeController extends AbstractOrganisationCreationController {

    private static final String ORGANISATION_TYPE_ID = "organisationTypeId";

    protected static final String NOT_ELIGIBLE = "not-eligible";

    @Autowired
    private CompetitionRestService competitionRestService;

    @GetMapping
    public String selectOrganisationType(Model model,
                                         HttpServletRequest request) {
        Optional<OrganisationCreationForm> organisationCreationFormCookie = registrationCookieService.getOrganisationCreationCookieValue(request);
        if (organisationCreationFormCookie.isPresent()) {
            model.addAttribute(ORGANISATION_FORM, organisationCreationFormCookie.get());
        } else {
            model.addAttribute(ORGANISATION_FORM, new OrganisationCreationForm());
        }


        return TEMPLATE_PATH + "/" + ORGANISATION_TYPE;
    }

    @PostMapping
    public String confirmSelectOrganisationType(Model model,
                                                @Valid @ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                                BindingResult bindingResult,
                                                HttpServletRequest request,
                                                HttpServletResponse response) {
        if (!bindingResult.hasFieldErrors(ORGANISATION_TYPE_ID)) {
            EuOrganisationType organisationType = organisationForm.getOrganisationType();
            OrganisationTypeForm organisationTypeForm = registrationCookieService.getOrganisationTypeCookieValue(request).orElse(new OrganisationTypeForm());
            organisationTypeForm.setOrganisationType(organisationType);
            registrationCookieService.saveToOrganisationTypeCookie(organisationTypeForm, response);
            saveOrganisationTypeToCreationForm(response, organisationTypeForm);
            return "redirect:" + BASE_URL + "/" + FIND_ORGANISATION;
        } else {
            organisationForm.setTriedToSave(true);
            return TEMPLATE_PATH + "/" + ORGANISATION_TYPE;
        }
    }

    private void saveOrganisationTypeToCreationForm(HttpServletResponse response, OrganisationTypeForm organisationTypeForm) {
        OrganisationCreationForm newOrganisationCreationForm = new OrganisationCreationForm();
        newOrganisationCreationForm.setOrganisationType(organisationTypeForm.getOrganisationType());
        registrationCookieService.saveToOrganisationCreationCookie(newOrganisationCreationForm, response);
    }
}