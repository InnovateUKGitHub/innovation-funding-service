package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.KtpYearResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesKtpYearsResource;
import org.innovateuk.ifs.finance.service.YourOrganisationRestService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputResponseRestServiceImpl;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Saver for ktp.
 */
@Component
public class YourOrganisationKtpFinancialYearsFormSaver {

    public ServiceResult<Void> save(long targetId, long organisationId, long userId, YourOrganisationKtpFinancialYearsForm form, YourOrganisationRestService service) {
            OrganisationFinancesKtpYearsResource finances = new OrganisationFinancesKtpYearsResource(userId,
                form.getOrganisationSize(),
                form.getYears().stream().map(year -> new KtpYearResource(
                        year.getYear(),
                        year.getTurnover(),
                        year.getPreTaxProfit(),
                        year.getCurrentAssets(),
                        form.isKtpPhase2Enabled() ? year.getCurrentAssets() : year.getLiabilities(),
                        year.getShareholderValue(),
                        year.getLoans(),
                        year.getEmployees(),
                        year.getCorporateGroupEmployees()
                )).collect(Collectors.toList()),
                form.getHasAdditionalInfoSection(),
                form.getAdditionalInfo(),
                form.getGroupEmployees(),
                form.getFinancialYearEnd());

        return service.updateOrganisationFinancesKtpYears(targetId, organisationId, finances);
    }

}
