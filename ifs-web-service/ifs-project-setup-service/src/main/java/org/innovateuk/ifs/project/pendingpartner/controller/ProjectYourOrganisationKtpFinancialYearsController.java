package org.innovateuk.ifs.project.pendingpartner.controller;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationKtpFinancialYearsForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationKtpFinancialYearsFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationKtpFinancialYearsFormSaver;
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
@RequestMapping("/project/{projectId}/organisation/{organisationId}/your-organisation/ktp-financial-years")
public class ProjectYourOrganisationKtpFinancialYearsController extends AbstractProjectYourOrganisationFormController<YourOrganisationKtpFinancialYearsForm> {

    @Autowired
    private YourOrganisationKtpFinancialYearsFormPopulator formPopulator;
    @Autowired
    private ProjectYourOrganisationRestService yourOrganisationRestService;
    @Autowired
    private YourOrganisationKtpFinancialYearsFormSaver saver;

    @Override
    protected String redirectToViewPage(long projectId, long organisationId) {
        return format("redirect:/project/%d/organisation/%d/your-organisation/ktp-financial-years",
                projectId,
                organisationId);
    }

    @Override
    protected YourOrganisationKtpFinancialYearsForm populateForm(long projectId, long organisationId) {
        return formPopulator.populate(yourOrganisationRestService.getOrganisationKtpYears(projectId, organisationId).getSuccess());
    }

    @Override
    protected String formFragment() {
        return "ktp-financial-years";
    }

    @Override
    protected void update(long projectId, long organisationId, YourOrganisationKtpFinancialYearsForm form) {
        saver.save(projectId, organisationId, form, yourOrganisationRestService);
    }
}