package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.service.YourOrganisationService;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.springframework.stereotype.Component;

import java.time.YearMonth;

/**
 * A populator to build a YourOrganisationForm
 */
@Component
public class YourOrganisationWithGrowthTableFormPopulator {

    private YourOrganisationService yourOrganisationService;

    public YourOrganisationWithGrowthTableFormPopulator(YourOrganisationService yourOrganisationService) {
        this.yourOrganisationService = yourOrganisationService;
    }

    public YourOrganisationWithGrowthTableForm populate(long applicationId, long competitionId, long organisationId) {

        Boolean stateAidAgreed =
                yourOrganisationService.getStateAidAgreed(applicationId).getSuccess();

        OrganisationSize organisationSize =
                yourOrganisationService.getOrganisationSize(applicationId, organisationId).getSuccess();

        YearMonth financialYearEnd =
                yourOrganisationService.getFinancialYearEnd(applicationId, competitionId, organisationId).getSuccess();

        Long annualTurnoverAtEndOfFinancialYear =
                yourOrganisationService.getAnnualTurnoverAtEndOfFinancialYear(applicationId, competitionId, organisationId).getSuccess();

        Long annualProfitsAtEndOfFinancialYear =
                yourOrganisationService.getAnnualProfitsAtEndOfFinancialYear(applicationId, competitionId, organisationId).getSuccess();

        Long annualExportAtEndOfFinancialYear =
                yourOrganisationService.getAnnualExportAtEndOfFinancialYear(applicationId, competitionId, organisationId).getSuccess();

        Long researchAndDevelopmentSpendAtEndOfFinancialYear =
                yourOrganisationService.getResearchAndDevelopmentSpendAtEndOfFinancialYear(applicationId, competitionId, organisationId).getSuccess();

        Long headCountAtLastFinancialYear =
                yourOrganisationService.getHeadCountAtLastFinancialYear(applicationId, competitionId, organisationId).getSuccess();

        return new YourOrganisationWithGrowthTableForm(
                organisationSize,
                stateAidAgreed,
                financialYearEnd,
                headCountAtLastFinancialYear,
                annualTurnoverAtEndOfFinancialYear,
                annualProfitsAtEndOfFinancialYear,
                annualExportAtEndOfFinancialYear,
                researchAndDevelopmentSpendAtEndOfFinancialYear);

        // TODO DW - readOnlyAllApplicantApplicationFinances

        // TODO DW - formInputViewModelGenerator.fromSection
    }
}
