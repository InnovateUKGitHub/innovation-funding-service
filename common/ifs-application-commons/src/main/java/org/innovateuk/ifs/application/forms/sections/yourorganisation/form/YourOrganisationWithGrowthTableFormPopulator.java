package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.springframework.stereotype.Component;

/**
 * A populator to build a YourOrganisationWithGrowthTableForm when a growth table is required.
 */
@Component
public class YourOrganisationWithGrowthTableFormPopulator {

    public YourOrganisationWithGrowthTableForm populate(OrganisationFinancesWithGrowthTableResource finances, OrganisationResource organisation) {
        YourOrganisationWithGrowthTableForm yourOrganisationWithGrowthTableForm = new YourOrganisationWithGrowthTableForm(
                finances.getOrganisationSize(),
                finances.getFinancialYearEnd(),
                finances.getHeadCountAtLastFinancialYear(),
                finances.getAnnualTurnoverAtLastFinancialYear(),
                finances.getAnnualProfitsAtLastFinancialYear(),
                finances.getAnnualExportAtLastFinancialYear(),
                finances.getResearchAndDevelopmentSpendAtLastFinancialYear());
        yourOrganisationWithGrowthTableForm.setOrganisation(organisation);
        return yourOrganisationWithGrowthTableForm;
    }
}
