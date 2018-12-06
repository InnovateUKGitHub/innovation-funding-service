package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.service.YourOrganisationService;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A populator to build a YourOrganisationForm
 */
@Component
public class YourOrganisationFormPopulator {

    private YourOrganisationService yourOrganisationService;

    public YourOrganisationFormPopulator(YourOrganisationService yourOrganisationService) {
        this.yourOrganisationService = yourOrganisationService;
    }

    public YourOrganisationForm populate(long applicationId, long competitionId, long organisationId) {

        boolean stateAidAgreed = yourOrganisationService.getStateAidAgreed(applicationId).getSuccess();
        OrganisationSize organisationSize = yourOrganisationService.getOrganisationSize(applicationId, organisationId).getSuccess();

        boolean includesGrowthTable = yourOrganisationService.isIncludingGrowthTable(competitionId).getSuccess();

        if (includesGrowthTable) {

            LocalDateTime financialYearEnd =
                    yourOrganisationService.getFinancialYearEnd(applicationId, competitionId, organisationId).getSuccess();

            List<GrowthTableRow> growthTableRows =
                    yourOrganisationService.getGrowthTableRows(applicationId, organisationId).getSuccess();

            Long headCountAtLastFinancialYear =
                    yourOrganisationService.getHeadCountAtLastFinancialYear(applicationId, organisationId).getSuccess();

            return YourOrganisationForm.withGrowthTable(organisationSize, null, stateAidAgreed, growthTableRows, financialYearEnd, headCountAtLastFinancialYear);

        } else {
            Long turnover = yourOrganisationService.getTurnover(applicationId, competitionId, organisationId).getSuccess();
            Long headCount = yourOrganisationService.getHeadCount(applicationId, competitionId, organisationId).getSuccess();
            return YourOrganisationForm.noGrowthTable(organisationSize, turnover, headCount, stateAidAgreed);
        }


        // TODO DW - readOnlyAllApplicantApplicationFinances

        // TODO DW - formInputViewModelGenerator.fromSection
    }
}
