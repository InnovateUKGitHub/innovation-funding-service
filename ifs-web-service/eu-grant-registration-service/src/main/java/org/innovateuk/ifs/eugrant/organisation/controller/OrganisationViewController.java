package org.innovateuk.ifs.eugrant.organisation.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.eugrant.EuOrganisationResource;
import org.innovateuk.ifs.eugrant.organisation.populator.OrganisationViewModelPopulator;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Provides methods for both:
 * Finding your company or research type organisation through Companies House or JES search.
 * Verifying or amending the address attached to the organisation.
 */
@Controller
@RequestMapping(AbstractOrganisationController.BASE_URL + "/" + AbstractOrganisationController.VIEW_ORGANISATION)
@SecuredBySpring(value = "Controller", description = "TODO", securedType = OrganisationViewController.class)
@PreAuthorize("permitAll")
public class OrganisationViewController extends AbstractOrganisationController {

    private static final Log LOG = LogFactory.getLog(OrganisationViewController.class);

    @Autowired
    private OrganisationViewModelPopulator organisationViewModelPopulator;

    @Autowired
    private EuGrantCookieService euGrantCookieService;

    @GetMapping
    public String view(Model model) {
        EuOrganisationResource organisation = euGrantCookieService.get().getOrganisation();
        if (organisation == null) {
            return "redirect:" + BASE_URL + "/" + ORGANISATION_TYPE;
        }
        model.addAttribute("model", organisationViewModelPopulator.populate(organisation));
        return TEMPLATE_PATH + "/" + VIEW_ORGANISATION;
    }
}
