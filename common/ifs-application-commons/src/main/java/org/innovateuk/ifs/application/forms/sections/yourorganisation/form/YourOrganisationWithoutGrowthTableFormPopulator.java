package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.springframework.stereotype.Component;

/**
 * A populator to build a YourOrganisationWithoutGrowthTableForm when a growth table is not required.
 */
@Component
public class YourOrganisationWithoutGrowthTableFormPopulator {

    public YourOrganisationWithoutGrowthTableForm populate(OrganisationFinancesWithoutGrowthTableResource finances, OrganisationResource organisation) {
        YourOrganisationWithoutGrowthTableForm yourOrganisationWithoutGrowthTableForm =  new YourOrganisationWithoutGrowthTableForm(
                finances.getOrganisationSize(),
                finances.getTurnover(),
                finances.getHeadCount());
        yourOrganisationWithoutGrowthTableForm.setOrganisation(organisation);
        return  yourOrganisationWithoutGrowthTableForm;
    }
}
