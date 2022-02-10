package org.innovateuk.ifs.project.pendingpartner.controller;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableFormSaver;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.lang.String.format;

/**
 * The Controller for the "Your organisation" page in the project setup process
 * when a a new partner has been invited and a growth table is not required.
 */
@Controller
@RequestMapping("/project/{projectId}/organisation/{organisationId}/your-organisation/without-growth-table")
public class ProjectYourOrganisationWithoutGrowthTableController extends AbstractProjectYourOrganisationFormController<YourOrganisationWithoutGrowthTableForm> {

    @Autowired
    private YourOrganisationWithoutGrowthTableFormPopulator withoutGrowthTableFormPopulator;
    @Autowired
    private ProjectYourOrganisationRestService yourOrganisationRestService;
    @Autowired
    private YourOrganisationWithoutGrowthTableFormSaver saver;

    @Override
    protected String redirectToViewPage(long projectId, long organisationId) {
        return format("redirect:/project/%d/organisation/%d/your-organisation/without-growth-table",
                projectId,
                organisationId);
    }

    @Override
    protected YourOrganisationWithoutGrowthTableForm populateForm(long projectId, long organisationId) {
        return withoutGrowthTableFormPopulator.populate(yourOrganisationRestService.getOrganisationFinancesWithoutGrowthTable(projectId, organisationId).getSuccess());
    }

    @Override
    protected String formFragment() {
        return "without-growth-table";
    }

    @Override
    protected void update(long projectId, long organisationId, YourOrganisationWithoutGrowthTableForm form) {
        saver.save(projectId, organisationId, form, yourOrganisationRestService);
    }
}