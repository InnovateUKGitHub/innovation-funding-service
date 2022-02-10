package org.innovateuk.ifs.project.organisationdetails.edit.controller;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableFormSaver;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/project/{projectId}/organisation/{organisationId}/edit/without-growth-table")
public class EditOrganisationDetailsWithoutGrowthTableController extends AbstractEditOrganisationDetailsController<YourOrganisationWithoutGrowthTableForm> {

    @Autowired
    private ProjectYourOrganisationRestService projectYourOrganisationRestService;

    @Autowired
    private YourOrganisationWithoutGrowthTableFormPopulator withoutGrowthTableFormPopulator;

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private YourOrganisationWithoutGrowthTableFormSaver saver;

    @Override
    protected String redirectToOrganisationDetails(long projectId, long organisationId) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        return "redirect:" + String.format("/competition/%d/project/%d/organisation/%d/details/without-growth-table", project.getCompetition(), projectId, organisationId);
    }
    @Override
    protected String formFragment() {
        return "without-growth-table";
    }

    @Override
    protected YourOrganisationWithoutGrowthTableForm form(long projectId, long organisationId) {
        OrganisationFinancesWithoutGrowthTableResource financesWithoutGrowthTable = projectYourOrganisationRestService.getOrganisationFinancesWithoutGrowthTable(projectId, organisationId).getSuccess();
        return withoutGrowthTableFormPopulator.populate(financesWithoutGrowthTable);
    }

    @Override
    protected ServiceResult<Void> update(long projectId, long organisationId, YourOrganisationWithoutGrowthTableForm form) {
        return saver.save(projectId, organisationId, form, projectYourOrganisationRestService);
    }
}