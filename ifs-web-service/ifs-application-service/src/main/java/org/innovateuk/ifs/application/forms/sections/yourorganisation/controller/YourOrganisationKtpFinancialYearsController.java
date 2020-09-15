package org.innovateuk.ifs.application.forms.sections.yourorganisation.controller;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationKtpFinancialYearsForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationKtpFinancialYearsFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationKtpFinancialYearsFormSaver;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesKtpYearsResource;
import org.innovateuk.ifs.finance.service.ApplicationYourOrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

/**
 * The Controller for the "Your organisation" page in the Application Form process when its a ktp competition.
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/your-organisation/competition/{competitionId}/organisation/{organisationId}/section/{sectionId}/ktp-financial-years")
public class YourOrganisationKtpFinancialYearsController extends AbstractYourOrganisationFormController<YourOrganisationKtpFinancialYearsForm> {

    @Autowired
    private ApplicationYourOrganisationRestService yourOrganisationRestService;

    @Autowired
    private YourOrganisationKtpFinancialYearsFormPopulator formPopulator;

    @Autowired
    private YourOrganisationKtpFinancialYearsFormSaver saver;

    @Override
    protected YourOrganisationKtpFinancialYearsForm populateForm(long applicationId, long organisationId) {
        OrganisationFinancesKtpYearsResource finances = yourOrganisationRestService.getOrganisationKtpYears(applicationId, organisationId).getSuccess();
        return formPopulator.populate(finances);
    }

    @Override
    protected String formFragment() {
        return "ktp-financial-years";
    }

    @Override
    protected void update(long applicationId, long organisationId, YourOrganisationKtpFinancialYearsForm form) {
         saver.save(applicationId, organisationId, form, yourOrganisationRestService);
    }

    @Override
    protected String redirectToViewPage(long applicationId, long competitionId, long organisationId, long sectionId) {
        return "redirect:" + APPLICATION_BASE_URL +
                String.format("%d/form/your-organisation/competition/%d/organisation/%d/section/%d/ktp-financial-years",
                        applicationId,
                        competitionId,
                        organisationId,
                        sectionId);
    }
}
