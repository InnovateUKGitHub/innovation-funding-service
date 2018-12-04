package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.service.YourOrganisationService;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.springframework.stereotype.Component;

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

        OrganisationSize organisationSize = yourOrganisationService.getOrganisationSize(applicationId, organisationId).getSuccess();

        Long turnover = yourOrganisationService.getTurnover(applicationId, competitionId, organisationId).getSuccess();

        Long headcount = yourOrganisationService.getHeadCount(applicationId, competitionId, organisationId).getSuccess();

        boolean stateAidEligibility = yourOrganisationService.getStateAidEligibility(applicationId).getSuccess();

        // TODO DW - readOnlyAllApplicantApplicationFinances

        // TODO DW - isBusinessOrganisation

        // TODO DW - formInputViewModelGenerator.fromSection

        return new YourOrganisationForm(
                organisationSize,
                turnover,
                headcount,
                stateAidEligibility);
    }
}
