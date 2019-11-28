package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.springframework.stereotype.Component;

/**
 * A populator to build a YourOrganisationWithGrowthTableForm when a growth table is required.
 */
@Component
public class YourOrganisationWithGrowthTableFormPopulator {

    public YourOrganisationWithGrowthTableForm populate(OrganisationFinancesWithGrowthTableResource finances) {
        return new YourOrganisationWithGrowthTableForm(
                finances.getOrganisationSize(),
                finances.getStateAidAgreed(),
                finances.getFinancialYearEnd(),
                finances.getHeadCountAtLastFinancialYear(),
                finances.getAnnualTurnoverAtLastFinancialYear(),
                finances.getAnnualProfitsAtLastFinancialYear(),
                finances.getAnnualExportAtLastFinancialYear(),
                finances.getResearchAndDevelopmentSpendAtLastFinancialYear());
    }
}
