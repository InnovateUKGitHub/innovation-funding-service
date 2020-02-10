package org.innovateuk.ifs.project.organisationdetails.view.controller;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableFormPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller will allow the user to view organisation details without a growth table.
 */
@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/organisation/{organisationId}/details/without-growth-table")
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
@SecuredBySpring(value = "Controller", description = "Internal users can view organisation details",
        securedType = OrganisationDetailsWithoutGrowthTableController.class)
public class OrganisationDetailsWithoutGrowthTableController extends AbstractOrganisationDetailsController<YourOrganisationWithoutGrowthTableForm> {

    @Autowired
    private ProjectYourOrganisationRestService projectYourOrganisationRestService;

    @Autowired
    private YourOrganisationWithoutGrowthTableFormPopulator withoutGrowthTableFormPopulator;

    @Override
    protected String getView() {
        return "project/organisationdetails/organisation-details-without-growth-table";
    }

    @Override
    protected YourOrganisationWithoutGrowthTableForm getForm(long projectId, long organisationId) {
        return withoutGrowthTableFormPopulator.populate(projectYourOrganisationRestService.getOrganisationFinancesWithoutGrowthTable(projectId, organisationId).getSuccess());
    }
}