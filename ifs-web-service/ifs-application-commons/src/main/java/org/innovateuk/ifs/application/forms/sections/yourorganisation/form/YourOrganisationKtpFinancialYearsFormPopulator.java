package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.innovateuk.ifs.finance.resource.OrganisationFinancesKtpYearsResource;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * A populator to build a YourOrganisationWithGrowthTableForm when a growth table is required.
 */
@Component
public class YourOrganisationKtpFinancialYearsFormPopulator {

    public YourOrganisationKtpFinancialYearsForm populate(OrganisationFinancesKtpYearsResource finances) {
        return new YourOrganisationKtpFinancialYearsForm(
                finances.getOrganisationSize(),
                finances.getYears().stream().map(year ->
                        new YourOrganisationKtpFinancialYearForm(
                                year.getYear(),
                                year.getTurnover(),
                                year.getPreTaxProfit(),
                                year.getCurrentAssets(),
                                year.getLiabilities(),
                                year.getShareholderValue(),
                                year.getLoans(),
                                year.getEmployees()
                        )
                    ).collect(Collectors.toList()),
                finances.getGroupEmployees());
    }
}
