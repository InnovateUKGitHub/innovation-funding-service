package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
import org.springframework.stereotype.Component;

/**
 * A populator to build a YourOrganisationWithoutGrowthTableForm when a growth table is not required.
 */
@Component
public class YourOrganisationWithoutGrowthTableFormPopulator {

    public YourOrganisationWithoutGrowthTableForm populate(OrganisationFinancesWithoutGrowthTableResource finances) {
        return new YourOrganisationWithoutGrowthTableForm(
                finances.getOrganisationSize(),
                finances.getTurnover(),
                finances.getHeadCount(),
                finances.getStateAidAgreed());
    }
}
