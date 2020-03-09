package org.innovateuk.ifs.project.organisationdetails.view.controller;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableFormPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller will allow the user to view organisation details with a growth table.
 */
@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/organisation/{organisationId}/details/with-growth-table")
@SecuredBySpring(value = "Controller", description = "Internal users can view organisation details",
        securedType = OrganisationDetailsWithGrowthTableController.class)
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
public class OrganisationDetailsWithGrowthTableController extends AbstractOrganisationDetailsController<YourOrganisationWithGrowthTableForm> {

    @Autowired
    private ProjectYourOrganisationRestService projectYourOrganisationRestService;

    @Autowired
    private YourOrganisationWithGrowthTableFormPopulator withGrowthTableFormPopulator;

    @Override
    protected String getView() {
        return "project/organisationdetails/organisation-details-with-growth-table";
    }

    @Override
    protected YourOrganisationWithGrowthTableForm getForm(long projectId, long organisationId) {
        return withGrowthTableFormPopulator.populate(projectYourOrganisationRestService.getOrganisationFinancesWithGrowthTable(projectId, organisationId).getSuccess());
    }
}