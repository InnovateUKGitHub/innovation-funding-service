package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.service.CompetitionOrganisationConfigRestService;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * Provides the initialization method and redirect when registering a new organisation as a lead applicant.
 */
@Controller
@RequestMapping(AbstractOrganisationCreationController.BASE_URL + "/initialize")
@SecuredBySpring(value = "Controller", description = "Anyone can start the lead applicant journey.", securedType = OrganisationCreationLeadInitializationController.class)
@PreAuthorize("permitAll")
public class OrganisationCreationLeadInitializationController extends AbstractOrganisationCreationController {

    @Autowired
    private CompetitionOrganisationConfigRestService competitionOrganisationConfigRestService;

    @GetMapping
    public String initializeLeadRegistrationJourney(HttpServletRequest request, HttpServletResponse response) {
        //This is the first endpoint when creating a new account as lead applicant.
        registrationCookieService.deleteOrganisationCreationCookie(response);
        // Implement properly once IFS-7194 has done in, for now set to true for all
        Optional<Long> competitionIdOpt = registrationCookieService.getCompetitionIdCookieValue(request);
        CompetitionOrganisationConfigResource organisationConfig = competitionOrganisationConfigRestService.findByCompetitionId(competitionIdOpt.get()).getSuccess();

        OrganisationTypeForm organisationTypeForm = new OrganisationTypeForm();
        organisationTypeForm.setLeadApplicant(true);
        registrationCookieService.saveToOrganisationTypeCookie(organisationTypeForm, response);

        if (organisationConfig.getInternationalLeadOrganisationAllowed()) {
            return "redirect:" + BASE_URL + "/" + INTERNATIONAL_ORGANISATION;
        }

        return "redirect:" + BASE_URL + "/" + LEAD_ORGANISATION_TYPE;
    }
}
