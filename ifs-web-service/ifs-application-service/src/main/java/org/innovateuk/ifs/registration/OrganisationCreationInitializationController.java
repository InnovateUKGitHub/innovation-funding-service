package org.innovateuk.ifs.registration;

import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Provides the initialization method and redirect when registering a new organisation.
 */
@Controller
@RequestMapping(AbstractOrganisationCreationController.BASE_URL + "/initialize")
@PreAuthorize("permitAll")
public class OrganisationCreationInitializationController extends AbstractOrganisationCreationController {
    @GetMapping
    public String createOrganisationAsLeadApplicant(HttpServletRequest request, HttpServletResponse response) {
        //This is the first endpoint when creating a new account as lead applicant.
        registrationCookieService.deleteOrganisationCreationCookie(response);

        OrganisationTypeForm organisationTypeForm = new OrganisationTypeForm();
        organisationTypeForm.setLeadApplicant(true);
        registrationCookieService.saveToOrganisationTypeCookie(organisationTypeForm, response);
        return "redirect:" + BASE_URL + "/" + LEAD_ORGANISATION_TYPE;
    }
}
