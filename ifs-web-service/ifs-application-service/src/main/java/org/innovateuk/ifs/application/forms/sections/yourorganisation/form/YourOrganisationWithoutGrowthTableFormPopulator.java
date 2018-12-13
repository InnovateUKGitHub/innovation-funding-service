package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.service.YourOrganisationService;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.springframework.stereotype.Component;

/**
 * A populator to build a YourOrganisationForm
 */
@Component
public class YourOrganisationWithoutGrowthTableFormPopulator {

    private YourOrganisationService yourOrganisationService;

    public YourOrganisationWithoutGrowthTableFormPopulator(YourOrganisationService yourOrganisationService) {
        this.yourOrganisationService = yourOrganisationService;
    }

    public YourOrganisationWithoutGrowthTableForm populate(long applicationId, long competitionId, long organisationId) {

        Boolean stateAidAgreed = yourOrganisationService.getStateAidAgreed(applicationId).getSuccess();
        OrganisationSize organisationSize = yourOrganisationService.getOrganisationSize(applicationId, organisationId).getSuccess();

        Long turnover = yourOrganisationService.getTurnover(applicationId, competitionId, organisationId).getSuccess();
        Long headCount = yourOrganisationService.getHeadCount(applicationId, competitionId, organisationId).getSuccess();
        return new YourOrganisationWithoutGrowthTableForm(organisationSize, turnover, headCount, stateAidAgreed);

        // TODO DW - readOnlyAllApplicantApplicationFinances

        // TODO DW - formInputViewModelGenerator.fromSection
    }
}
