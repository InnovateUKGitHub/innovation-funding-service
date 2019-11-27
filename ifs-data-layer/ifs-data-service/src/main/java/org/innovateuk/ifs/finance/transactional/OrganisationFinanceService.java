package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;

public interface OrganisationFinanceService {
    @NotSecured(value = "Service should only be calling other services to receive data and should be using their permission rules.", mustBeSecuredByOtherServices = false)
    ServiceResult<OrganisationFinancesWithGrowthTableResource> getOrganisationWithGrowthTable(long targetId, long organisationId);

    @NotSecured(value = "Service should only be calling other services to receive data and should be using their permission rules.", mustBeSecuredByOtherServices = false)
    ServiceResult<OrganisationFinancesWithoutGrowthTableResource> getOrganisationWithoutGrowthTable(long targetId, long organisationId);

    @NotSecured(value = "Service should only be calling other services to receive data and should be using their permission rules.", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> updateOrganisationWithGrowthTable(long targetId, long organisationId, OrganisationFinancesWithGrowthTableResource finances);

    @NotSecured(value = "Service should only be calling other services to receive data and should be using their permission rules.", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> updateOrganisationWithoutGrowthTable(long targetId, long organisationId, OrganisationFinancesWithoutGrowthTableResource finances);

    @NotSecured(value = "Service should only be calling other services to receive data and should be using their permission rules.", mustBeSecuredByOtherServices = false)
    ServiceResult<Boolean> isShowStateAidAgreement(long targetId, long organisationId);
}