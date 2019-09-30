package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;

public interface OrganisationFinanceService {

    ServiceResult<OrganisationFinancesWithGrowthTableResource> getOrganisationWithGrowthTable(long applicationId, long organisationId);

    ServiceResult<OrganisationFinancesWithoutGrowthTableResource> getOrganisationWithoutGrowthTable(long applicationId, long organisationId);

    ServiceResult<Void> updateOrganisationWithGrowthTable(long applicationId, long organisationId, OrganisationFinancesWithGrowthTableResource finances);

    ServiceResult<Void> updateOrganisationWithoutGrowthTable(long applicationId, long organisationId, OrganisationFinancesWithoutGrowthTableResource finances);

    ServiceResult<Boolean> isShowStateAidAgreement(long applicationId, long organisationId);
}