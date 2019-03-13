package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.service.YourOrganisationRestService;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
import org.springframework.stereotype.Component;

/**
 * A populator to build a YourOrganisationWithoutGrowthTableForm when a growth table is not required.
 */
@Component
public class YourOrganisationWithoutGrowthTableFormPopulator {

    private YourOrganisationRestService yourOrganisationRestService;

    public YourOrganisationWithoutGrowthTableFormPopulator(YourOrganisationRestService yourOrganisationRestService) {
        this.yourOrganisationRestService = yourOrganisationRestService;
    }

    public YourOrganisationWithoutGrowthTableForm populate(long applicationId, long organisationId) {

        OrganisationFinancesWithoutGrowthTableResource finances =
                yourOrganisationRestService.getOrganisationFinancesWithoutGrowthTable(applicationId, organisationId).
                getSuccess();

        return new YourOrganisationWithoutGrowthTableForm(
                finances.getOrganisationSize(),
                finances.getTurnover(),
                finances.getHeadCount(),
                finances.getStateAidAgreed());

        // TODO DW - readOnlyAllApplicantApplicationFinances

        // TODO DW - formInputViewModelGenerator.fromSection
    }
}
