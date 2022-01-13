package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.KtpYearResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesKtpYearsResource;
import org.innovateuk.ifs.finance.service.YourOrganisationRestService;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Saver for ktp.
 */
@Component
public class YourOrganisationKtpFinancialYearsFormSaver {

    public ServiceResult<Void> save(long targetId, long organisationId, YourOrganisationKtpFinancialYearsForm form, YourOrganisationRestService service) {
            OrganisationFinancesKtpYearsResource finances = new OrganisationFinancesKtpYearsResource(
                form.getOrganisationSize(),
                form.getYears().stream().map(year -> new KtpYearResource(
                        year.getYear(),
                        year.getTurnover(),
                        year.getPreTaxProfit(),
                        year.getCurrentAssets(),
                        year.getLiabilities(),
                        year.getShareholderValue(),
                        year.getLoans(),
                        year.getEmployees()
                )).collect(Collectors.toList()),
                form.getGroupEmployees(),
                form.getFinancialYearEnd());

        return service.updateOrganisationFinancesKtpYears(targetId, organisationId, finances);
    }
}
