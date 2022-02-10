package org.innovateuk.ifs.project.organisationdetails.edit.controller;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableFormSaver;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/project/{projectId}/organisation/{organisationId}/edit/with-growth-table")
public class EditOrganisationDetailsWithGrowthTableController extends AbstractEditOrganisationDetailsController<YourOrganisationWithGrowthTableForm> {

    @Autowired
    private ProjectYourOrganisationRestService projectYourOrganisationRestService;

    @Autowired
    private YourOrganisationWithGrowthTableFormPopulator withGrowthTableFormPopulator;

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private YourOrganisationWithGrowthTableFormSaver saver;

    @Override
    protected String redirectToOrganisationDetails(long projectId, long organisationId) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        return "redirect:" + String.format("/competition/%d/project/%d/organisation/%d/details/with-growth-table", project.getCompetition(), projectId, organisationId);
    }

    @Override
    protected String formFragment() {
        return "with-growth-table";
    }

    @Override
    protected YourOrganisationWithGrowthTableForm form(long projectId, long organisationId) {
        OrganisationFinancesWithGrowthTableResource financesWithGrowthTable = projectYourOrganisationRestService.getOrganisationFinancesWithGrowthTable(projectId, organisationId).getSuccess();
        return withGrowthTableFormPopulator.populate(financesWithGrowthTable);
    }

    @Override
    protected ServiceResult<Void> update(long projectId, long organisationId, YourOrganisationWithGrowthTableForm form) {
        return saver.save(projectId, organisationId, form, projectYourOrganisationRestService);
    }
}