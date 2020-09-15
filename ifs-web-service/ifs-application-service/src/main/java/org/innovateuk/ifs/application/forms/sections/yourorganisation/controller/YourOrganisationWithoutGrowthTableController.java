package org.innovateuk.ifs.application.forms.sections.yourorganisation.controller;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableFormSaver;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
import org.innovateuk.ifs.finance.service.ApplicationYourOrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

/**
 * The Controller for the "Your organisation" page in the Application Form process when a growth table is not required.
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/your-organisation/competition/{competitionId}/organisation/{organisationId}/section/{sectionId}/without-growth-table")
public class YourOrganisationWithoutGrowthTableController extends AbstractYourOrganisationFormController<YourOrganisationWithoutGrowthTableForm> {
    @Autowired
    private YourOrganisationWithoutGrowthTableFormPopulator withoutGrowthTableFormPopulator;

    @Autowired
    private ApplicationYourOrganisationRestService yourOrganisationRestService;

    @Autowired
    private YourOrganisationWithoutGrowthTableFormSaver saver;

    @Override
    protected YourOrganisationWithoutGrowthTableForm populateForm(long applicationId, long organisationId) {
        OrganisationFinancesWithoutGrowthTableResource finances = yourOrganisationRestService.getOrganisationFinancesWithoutGrowthTable(applicationId, organisationId).getSuccess();
        return withoutGrowthTableFormPopulator.populate(finances);
    }

    @Override
    protected String formFragment() {
        return "without-growth-table";
    }

    @Override
    protected void update(long applicationId, long organisationId, YourOrganisationWithoutGrowthTableForm form) {
        saver.save(applicationId, organisationId, form, yourOrganisationRestService);
    }

    @Override
    protected String redirectToViewPage(long applicationId, long competitionId, long organisationId, long sectionId) {
        return "redirect:" + APPLICATION_BASE_URL +
                String.format("%d/form/your-organisation/competition/%d/organisation/%d/section/%d/without-growth-table",
                        applicationId,
                        competitionId,
                        organisationId,
                        sectionId);
    }
}