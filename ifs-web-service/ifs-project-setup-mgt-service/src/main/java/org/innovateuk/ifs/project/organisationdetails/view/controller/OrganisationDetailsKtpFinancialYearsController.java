package org.innovateuk.ifs.project.organisationdetails.view.controller;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationKtpFinancialYearsForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationKtpFinancialYearsFormPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller will allow the user to view organisation details with a growth table.
 */
@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/organisation/{organisationId}/details/ktp-financial-years")
@SecuredBySpring(value = "Controller", description = "Internal users can view organisation details",
        securedType = OrganisationDetailsKtpFinancialYearsController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'support', 'innovation_lead', 'stakeholder')")
public class OrganisationDetailsKtpFinancialYearsController extends AbstractOrganisationDetailsController<YourOrganisationKtpFinancialYearsForm> {

    @Autowired
    private ProjectYourOrganisationRestService projectYourOrganisationRestService;

    @Autowired
    private YourOrganisationKtpFinancialYearsFormPopulator formPopulator;

    @Override
    protected String formFragment() {
        return "ktp-financial-years";
    }

    @Autowired
    private OrganisationRestService organisationRestService;

    @Override
    protected YourOrganisationKtpFinancialYearsForm getForm(long projectId, long organisationId) {
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        return formPopulator.populate(projectYourOrganisationRestService.getOrganisationKtpYears(projectId, organisationId).getSuccess(), organisation);
    }
}