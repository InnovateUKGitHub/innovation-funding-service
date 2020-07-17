package org.innovateuk.ifs.project.organisationdetails.edit.controller;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationKtpFinancialYearsForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationKtpFinancialYearsFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationKtpFinancialYearsFormSaver;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesKtpYearsResource;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/project/{projectId}/organisation/{organisationId}/edit/ktp-financial-years")
public class EditOrganisationDetailsControlleKtpFinancialYears extends AbstractEditOrganisationDetailsController<YourOrganisationKtpFinancialYearsForm> {

    @Autowired
    private ProjectYourOrganisationRestService projectYourOrganisationRestService;

    @Autowired
    private YourOrganisationKtpFinancialYearsFormPopulator formPopulator;

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private YourOrganisationKtpFinancialYearsFormSaver saver;

    @Override
    protected String redirectToOrganisationDetails(long projectId, long organisationId) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        return "redirect:" + String.format("/competition/%d/project/%d/organisation/%d/details/ktp-financial-years", project.getCompetition(), projectId, organisationId);
    }

    @Override
    protected String formFragment() {
        return "ktp-financial-years";
    }

    @Override
    protected YourOrganisationKtpFinancialYearsForm form(long projectId, long organisationId) {
        OrganisationFinancesKtpYearsResource financesWithGrowthTable = projectYourOrganisationRestService.getOrganisationKtpYears(projectId, organisationId).getSuccess();
        return formPopulator.populate(financesWithGrowthTable);
    }

    @Override
    protected ServiceResult<Void> update(long projectId, long organisationId, YourOrganisationKtpFinancialYearsForm form) {
       return saver.save(projectId, organisationId, form, projectYourOrganisationRestService);
    }
}