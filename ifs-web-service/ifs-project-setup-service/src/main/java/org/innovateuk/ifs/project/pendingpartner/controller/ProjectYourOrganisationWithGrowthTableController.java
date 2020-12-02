package org.innovateuk.ifs.project.pendingpartner.controller;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableFormSaver;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.lang.String.format;

/**
 * The Controller for the "Your organisation" page in the project setup process
 * when a a new partner has been invited and a growth table is required.
 */
@Controller
@RequestMapping("/project/{projectId}/organisation/{organisationId}/your-organisation/with-growth-table")
public class ProjectYourOrganisationWithGrowthTableController extends AbstractProjectYourOrganisationFormController<YourOrganisationWithGrowthTableForm> {

    @Autowired
    private YourOrganisationWithGrowthTableFormPopulator withGrowthTableFormPopulator;
    @Autowired
    private ProjectYourOrganisationRestService yourOrganisationRestService;
    @Autowired
    private YourOrganisationWithGrowthTableFormSaver saver;

    @Override
    protected String redirectToViewPage(long projectId, long organisationId) {
        return format("redirect:/project/%d/organisation/%d/your-organisation/with-growth-table",
                projectId,
                organisationId);
    }

    @Override
    protected YourOrganisationWithGrowthTableForm populateForm(long projectId, long organisationId) {
        return withGrowthTableFormPopulator.populate(yourOrganisationRestService.getOrganisationFinancesWithGrowthTable(projectId, organisationId).getSuccess());
    }

    @Override
    protected String formFragment() {
        return "with-growth-table";
    }

    @Override
    protected void update(long projectId, long organisationId, YourOrganisationWithGrowthTableForm form) {
        saver.save(projectId, organisationId, form, yourOrganisationRestService);
    }
}
