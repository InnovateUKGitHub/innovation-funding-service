package org.innovateuk.ifs.application.forms.sections.yourorganisation.controller;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableFormSaver;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.finance.service.ApplicationYourOrganisationRestService;
import org.innovateuk.ifs.finance.service.YourOrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

/**
 * The Controller for the "Your organisation" page in the Application Form process when a growth table is required.
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/your-organisation/competition/{competitionId}/organisation/{organisationId}/section/{sectionId}/with-growth-table")
public class YourOrganisationWithGrowthTableController extends AbstractYourOrganisationFormController<YourOrganisationWithGrowthTableForm> {

    @Autowired
    private YourOrganisationWithGrowthTableFormPopulator withGrowthTableFormPopulator;

    @Autowired
    private ApplicationYourOrganisationRestService yourOrganisationRestService;

    @Autowired
    private YourOrganisationWithGrowthTableFormSaver saver;

    @Override
    protected YourOrganisationWithGrowthTableForm populateForm(long applicationId, long organisationId) {
        OrganisationFinancesWithGrowthTableResource finances = yourOrganisationRestService.getOrganisationFinancesWithGrowthTable(applicationId, organisationId).getSuccess();
        return withGrowthTableFormPopulator.populate(finances);
    }

    @Override
    protected String formFragment() {
        return "with-growth-table";
    }

    @Override
    protected void update(long applicationId, long organisationId, YourOrganisationWithGrowthTableForm form) {
        saver.save(applicationId, organisationId, form, yourOrganisationRestService);
    }

    @Override
    protected String redirectToViewPage(long applicationId, long competitionId, long organisationId, long sectionId) {
        return "redirect:" + APPLICATION_BASE_URL +
                String.format("%d/form/your-organisation/competition/%d/organisation/%d/section/%d/with-growth-table",
                        applicationId,
                        competitionId,
                        organisationId,
                        sectionId);
    }

}
