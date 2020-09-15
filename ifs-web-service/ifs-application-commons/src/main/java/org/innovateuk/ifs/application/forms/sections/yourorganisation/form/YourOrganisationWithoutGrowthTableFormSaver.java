package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
import org.innovateuk.ifs.finance.service.YourOrganisationRestService;
import org.springframework.stereotype.Component;

/**
 * Saver for without growth table.
 */
@Component
public class YourOrganisationWithoutGrowthTableFormSaver {

    public ServiceResult<Void> save(long targetId, long organisationId, YourOrganisationWithoutGrowthTableForm form, YourOrganisationRestService service) {
        OrganisationFinancesWithoutGrowthTableResource finances = new OrganisationFinancesWithoutGrowthTableResource(
                form.getOrganisationSize(),
                form.getTurnover(),
                form.getHeadCount());

        return service.updateOrganisationFinancesWithoutGrowthTable(targetId, organisationId, finances);
    }
}
