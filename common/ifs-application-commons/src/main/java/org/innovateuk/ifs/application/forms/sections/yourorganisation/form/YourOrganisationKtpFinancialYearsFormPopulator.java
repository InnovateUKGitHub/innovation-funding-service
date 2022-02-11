package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.innovateuk.ifs.finance.resource.OrganisationFinancesKtpYearsResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * A populator to build a YourOrganisationWithGrowthTableForm when a growth table is required.
 */
@Component
public class YourOrganisationKtpFinancialYearsFormPopulator {

    @Value("${ifs.ktp.phase2.enabled}")
    private Boolean ktpPhase2Enabled;

    public YourOrganisationKtpFinancialYearsForm populate(OrganisationFinancesKtpYearsResource finances) {
        return new YourOrganisationKtpFinancialYearsForm(
                ktpPhase2Enabled,
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
                                year.getEmployees(),
                                year.getCorporateGroupEmployees()
                        )
                    ).collect(Collectors.toList()),
                finances.getHasAdditionalInfoSection(),
                finances.getAdditionalInfo(),
                finances.getGroupEmployees(),
                finances.getFinancialYearEnd());
    }
}
