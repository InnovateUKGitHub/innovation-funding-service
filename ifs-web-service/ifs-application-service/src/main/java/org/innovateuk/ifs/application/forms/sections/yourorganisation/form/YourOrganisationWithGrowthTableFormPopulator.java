package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.service.YourOrganisationRestService;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.springframework.stereotype.Component;

/**
 * A populator to build a YourOrganisationForm
 */
@Component
public class YourOrganisationWithGrowthTableFormPopulator {

    private YourOrganisationRestService yourOrganisationRestService;

    public YourOrganisationWithGrowthTableFormPopulator(YourOrganisationRestService yourOrganisationRestService) {
        this.yourOrganisationRestService = yourOrganisationRestService;
    }

    public YourOrganisationWithGrowthTableForm populate(long applicationId, long organisationId) {

        OrganisationFinancesWithGrowthTableResource finances =
                yourOrganisationRestService.getOrganisationFinancesWithGrowthTable(applicationId, organisationId).
                        getSuccess();

        return new YourOrganisationWithGrowthTableForm(
                finances.getOrganisationSize(),
                finances.getStateAidAgreed(),
                finances.getFinancialYearEnd(),
                finances.getHeadCountAtLastFinancialYear(),
                finances.getAnnualTurnoverAtLastFinancialYear(),
                finances.getAnnualProfitsAtLastFinancialYear(),
                finances.getAnnualExportAtLastFinancialYear(),
                finances.getResearchAndDevelopmentSpendAtLastFinancialYear());

        // TODO DW - readOnlyAllApplicantApplicationFinances

        // TODO DW - formInputViewModelGenerator.fromSection
    }
}
