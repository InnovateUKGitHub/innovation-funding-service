package org.innovateuk.ifs.project.organisationsize.populator;

import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
import org.innovateuk.ifs.project.organisationsize.form.ProjectOrganisationSizeWithoutGrowthTableForm;
import org.springframework.stereotype.Component;

@Component
public class ProjectOrganisationSizeWithoutGrowthTableFormPopulator {

    public ProjectOrganisationSizeWithoutGrowthTableForm populate(OrganisationFinancesWithoutGrowthTableResource finances) {
        return new ProjectOrganisationSizeWithoutGrowthTableForm(finances.getOrganisationSize(),
                finances.getTurnover(),
                finances.getHeadCount());
    }
}
