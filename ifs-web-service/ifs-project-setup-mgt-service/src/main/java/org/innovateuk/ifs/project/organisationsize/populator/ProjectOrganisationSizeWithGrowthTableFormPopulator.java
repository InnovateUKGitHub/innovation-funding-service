package org.innovateuk.ifs.project.organisationsize.populator;

import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.project.organisationsize.form.ProjectOrganisationSizeWithGrowthTableForm;
import org.springframework.stereotype.Component;

@Component
public class ProjectOrganisationSizeWithGrowthTableFormPopulator {

    public ProjectOrganisationSizeWithGrowthTableForm populate(OrganisationFinancesWithGrowthTableResource finances) {
        return new ProjectOrganisationSizeWithGrowthTableForm(
                finances.getOrganisationSize(),
                finances.getFinancialYearEnd(),
                finances.getHeadCountAtLastFinancialYear(),
                finances.getAnnualTurnoverAtLastFinancialYear(),
                finances.getAnnualProfitsAtLastFinancialYear(),
                finances.getAnnualExportAtLastFinancialYear(),
                finances.getResearchAndDevelopmentSpendAtLastFinancialYear());
    }
}
