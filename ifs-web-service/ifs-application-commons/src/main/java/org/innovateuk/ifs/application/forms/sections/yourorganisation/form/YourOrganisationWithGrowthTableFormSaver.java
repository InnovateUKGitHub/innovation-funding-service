package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.finance.service.YourOrganisationRestService;
import org.springframework.stereotype.Component;

/**
 * Saver for grwoth table.
 */
@Component
public class YourOrganisationWithGrowthTableFormSaver {

    public ServiceResult<Void> save(long targetId, long organisationId, YourOrganisationWithGrowthTableForm form, YourOrganisationRestService service) {
        OrganisationFinancesWithGrowthTableResource finances = new OrganisationFinancesWithGrowthTableResource(
                form.getOrganisationSize(),
                form.getFinancialYearEnd(),
                form.getHeadCountAtLastFinancialYear(),
                form.getAnnualTurnoverAtLastFinancialYear(),
                form.getAnnualProfitsAtLastFinancialYear(),
                form.getAnnualExportAtLastFinancialYear(),
                form.getResearchAndDevelopmentSpendAtLastFinancialYear());

        return service.updateOrganisationFinancesWithGrowthTable(targetId, organisationId, finances);
    }
}
